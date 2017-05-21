package eu.ggnet.dwoss.stock.test;


import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionPosition;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatus;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import java.util.Date;
import java.util.Set;

import javax.validation.*;

import org.junit.Test;

import static eu.ggnet.dwoss.stock.entity.StockTransactionStatusType.*;
import static org.junit.Assert.*;

/**
 *
 */
public class ValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testStockUnit() {

        StockUnit su = new StockUnit("Bla", 0);
        Set<? extends ConstraintViolation> violations = validator.validate(su);
        if ( violations.isEmpty() ) fail("No violation, but StockUnit has neither Stock nor Position");

        Stock s = new Stock(1, "Teststock");
        su.setStock(s);
        violations = validator.validate(su);
        if ( !violations.isEmpty() ) fail("Violation, but StockUnit has Stock. msg: " + buildMessage(violations));

        su.setStock(null);
        violations = validator.validate(su);
        if ( violations.isEmpty() ) fail("No violation, but StockUnit has neither Stock nor Position");

        StockTransactionPosition stp = new StockTransactionPosition(su);

        if ( validator.validate(su).isEmpty() ) fail("No violation, but StockUnit has invalid Position");

        StockTransaction t = new StockTransaction();
        t.addPosition(stp);
        if ( validator.validate(su).isEmpty() ) fail("No violation, but StockTransaction is still invalid (no status, no type)");

        t.addStatus(new StockTransactionStatus(StockTransactionStatusType.PREPARED, new Date()));
        if ( validator.validate(su).isEmpty() ) fail("No violation, but StockTransaction is still invalid (no type)");

        t.setType(StockTransactionType.TRANSFER);
        violations = validator.validate(su);
        if ( !violations.isEmpty() ) fail("Violation, but StockUnit has valid Position and Transaction. msg: " + buildMessage(violations));

        su.setStock(s);

        violations = validator.validate(su);
        if ( !violations.isEmpty() )
            fail("Violation, Special Case: StockUnit has Stock and Transaction(Prepared) which is ok. msg: " + buildMessage(violations));

        t.addStatus(new StockTransactionStatus(StockTransactionStatusType.COMMISSIONED, new Date()));
        if ( validator.validate(su).isEmpty() ) fail("No violation, but now StockUnit has Stock and StockTransaction( not prepared), which is not ok.");
    }

    @Test
    public void testValidTransaction() {
        StockTransaction t1 = new StockTransaction();
        t1.setType(StockTransactionType.TRANSFER);
        t1.addStatus(new StockTransactionStatus(PREPARED, new Date()));
        t1.addStatus(new StockTransactionStatus(COMMISSIONED, new Date()));
        t1.addStatus(new StockTransactionStatus(IN_TRANSFER, new Date()));
        t1.addStatus(new StockTransactionStatus(RECEIVED, new Date()));
        assertNull("Asserted null but it was:" + t1.getValidationViolations(), t1.getValidationViolations());
        t1.addStatus(new StockTransactionStatus(RECEIVED, new Date()));
        assertNotNull(t1.getValidationViolations());
    }

    private String buildMessage(Set<? extends ConstraintViolation> violations) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation violation : violations) {
            if ( violation.getPropertyPath().toString().endsWith("validationViolations") ) {
                sb.append(violation.getInvalidValue().toString());
            } else {
                sb.append("Validation Violation: ").append(violation.getPropertyPath()).append("=").append(violation.getInvalidValue()).append(",").append(violation.getMessage());
            }
        }
        return sb.toString();
    }
}
