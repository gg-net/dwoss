package eu.ggnet.dwoss.report.action;

import java.awt.event.ActionEvent;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.report.SimpleReportLineStage;

import javafx.application.Platform;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_RAW_REPORT_DATA;

public class ShowRawReportLinesAction extends AccessableAction {

    public ShowRawReportLinesAction() {
        super(READ_RAW_REPORT_DATA);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> new SimpleReportLineStage().show());
    }
}
