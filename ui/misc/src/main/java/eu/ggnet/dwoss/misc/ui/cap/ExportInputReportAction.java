/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;

import javafx.application.Platform;

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.misc.ui.cap.support.InputReportSelectorPane;
import eu.ggnet.dwoss.misc.ui.cap.support.OkCancelStage;
import eu.ggnet.dwoss.uniqueunit.ee.op.UniqueUnitReporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_INPUT_REPORT;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class ExportInputReportAction extends AccessableAction {

    public ExportInputReportAction() {
        super(EXPORT_INPUT_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> {
            final InputReportSelectorPane selector = new InputReportSelectorPane();
            OkCancelStage<InputReportSelectorPane> stage = new OkCancelStage<>("Auswählen", selector);
            stage.showAndWait();
            if ( stage.isCancel() ) return;
            Ui.exec(() -> {
                FileUtil.osOpen(Progressor.global().run(EXPORT_INPUT_REPORT.toName(),
                        () -> Dl.remote().lookup(UniqueUnitReporter.class).unitInputAsXls(selector.getStart(), selector.getEnd(), selector.getStep()).toTemporaryFile()));
            });
        });
    }
}
