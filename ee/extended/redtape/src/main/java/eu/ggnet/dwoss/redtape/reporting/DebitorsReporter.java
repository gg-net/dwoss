/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.redtape.reporting;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface DebitorsReporter {

    /**
     * Creates the Report
     *
     * @param start start intervall for the report
     * @param end end of the report intervall
     * @return a ByteArray represeting the content of an xls file.
     */
    FileJacket toXls(Date start, Date end);
}
