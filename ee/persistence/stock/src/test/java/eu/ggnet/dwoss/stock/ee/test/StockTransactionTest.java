package eu.ggnet.dwoss.stock.ee.test;

import java.util.Date;
import java.util.HashSet;

import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StockTransactionTest {

    private final Validator V = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testTransferValid() throws InterruptedException {
        StockTransaction tr1 = new StockTransaction(StockTransactionType.TRANSFER);
        addStatus(tr1, StockTransactionStatusType.PREPARED);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.COMMISSIONED);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.IN_TRANSFER);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.RECEIVED);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.COMPLETED);
        valid(tr1);
    }

    @Test
    public void testTransferInvalid() throws InterruptedException {
        StockTransaction tr1 = new StockTransaction(StockTransactionType.TRANSFER);
        addStatus(tr1, StockTransactionStatusType.PREPARED);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.IN_TRANSFER);
        invalid(tr1);
    }

    @Test
    public void testTransferInvalid2() throws InterruptedException {
        StockTransaction tr1 = new StockTransaction(StockTransactionType.TRANSFER);
        addStatus(tr1, StockTransactionStatusType.PREPARED);
        valid(tr1);
        addStatus(tr1, StockTransactionStatusType.PREPARED);
        invalid(tr1);
    }

    private void addStatus(StockTransaction st, StockTransactionStatusType type) throws InterruptedException {
        Date date = new Date();
        if ( st.getStatus() != null ) date = DateUtils.addSeconds(st.getStatus().getOccurence(), 1);
        st.addStatus(date, type, StockTransactionParticipationType.ARRANGER, "Test");
    }

    private void valid(StockTransaction tr1) {
        assertTrue("StockTransaction should be valid, but: " + ValidationUtil.formatToMultiLine(new HashSet<>(V.validate(tr1)), true),
                V.validate(tr1).isEmpty());
    }

    private void invalid(StockTransaction tr1) {
        assertFalse("StockTransaction should be invalid, but is valid: " + tr1,
                V.validate(tr1).isEmpty());
    }
}
