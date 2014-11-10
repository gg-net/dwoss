package eu.ggnet.dwoss.mandator.api.service;

import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;

import eu.ggnet.dwoss.report.api.MarginCalculator;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
public class MarginCalculatorBean implements MarginCalculator {

    @Override
    public void recalc(ViewReportResult report) {
        // we dont need to recalc
    }

}
