package eu.ggnet.dwoss.redtape.reporting;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface DirectDebitReporter {

    /**
     * Creates the Report
     * <p/>
     * @return a ByteArray represeting the content of an xls file.
     */
    FileJacket toXls();
}
