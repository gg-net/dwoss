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
package eu.ggnet.dwoss.misc.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import javafx.application.Platform;

import eu.ggnet.dwoss.common.ExceptionUtil;
import eu.ggnet.dwoss.misc.InputReportSelectorPane;
import eu.ggnet.dwoss.uniqueunit.op.UniqueUnitReporter;
import eu.ggnet.dwoss.util.OkCancelStage;
import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_INPUT_REPORT;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
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

            new SwingWorker<File, Object>() {
                @Override
                protected File doInBackground() throws Exception {
                    return lookup(UniqueUnitReporter.class).unitInputAsXls(selector.getStart(), selector.getEnd(), selector.getStep()).toTemporaryFile();
                }

                @Override
                protected void done() {
                    try {
                        Desktop.getDesktop().open(get());
                    } catch (InterruptedException | ExecutionException | IOException ex) {
                        ExceptionUtil.show(Client.lookup(Workspace.class).getMainFrame(), ex);
                    }
                }
            }.execute();
        });

    }
}