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
package eu.ggnet.dwoss.redtape.reporting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.reporting.DirectDebitReporter;

import eu.ggnet.dwoss.common.DwOssCore;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class DirectDebitReportAction extends AbstractAction {

    public DirectDebitReportAction() {
        super("Lastschriftenreport");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SwingWorker<File, Object>() {
            @Override
            protected File doInBackground() throws Exception {
                return lookup(DirectDebitReporter.class).toXls().toTemporaryFile();
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(get());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    DwOssCore.show(Client.lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
