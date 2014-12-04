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
package eu.ggnet.dwoss.redtape.action;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.redtape.gsoffice.GsOfficeExporter;

import eu.ggnet.saft.core.authorisation.AccessableAction;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.util.DateRangeChooserDialog;
import eu.ggnet.dwoss.common.DwOssCore;

import static eu.ggnet.saft.core.Client.lookup;
import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOCUMENTS_FOR_GSOFFICE_IN_XML;

/**
 * Action to create the GsOfficeXml.
 *
 * @author pascal.perau
 */
public class GsOfficeExportAction extends AccessableAction {

    public GsOfficeExportAction() {
        super(EXPORT_DOCUMENTS_FOR_GSOFFICE_IN_XML);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DateRangeChooserDialog dialog = new DateRangeChooserDialog(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                FileJacket jacket = lookup(GsOfficeExporter.class).toXml(dialog.getStart(), dialog.getEnd());
                jacket.toFile(GlobalConfig.APPLICATION_PATH_OUTPUT);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException | ExecutionException ex) {
                    DwOssCore.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();

    }
}
