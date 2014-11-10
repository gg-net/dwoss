package eu.ggnet.dwoss.redtape.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * Reporter of Credit Memos.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface CreditMemoReporter {

    FileJacket toXls(Date start, Date end);

    FileJacket toOptimizedXls(Date start, Date end);
}
