package eu.ggnet.dwoss.report.action;

import java.awt.event.ActionEvent;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.report.ExistingReportSelectionStage;

import javafx.application.Platform;

import static eu.ggnet.dwoss.rights.api.AtomicRight.READ_STORED_REPORTS;

/**
 *
 * @author pascal.perau
 */
public class ShowExistingReportAction extends AccessableAction {

    public ShowExistingReportAction() {
        super(READ_STORED_REPORTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> {
            ExistingReportSelectionStage stage = new ExistingReportSelectionStage();
            stage.show();
        });
    }
}
