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
package eu.ggnet.dwoss.price;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.FileUtil;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;
import eu.ggnet.saft.core.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.IMPORT_PRICEMANGMENT;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author pascal.perau
 */
public class PriceImportAction extends AccessableAction {

    public PriceImportAction() {
        super(IMPORT_PRICEMANGMENT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(null) ) {
            return;
        }
        final String fileName = dialog.getSelectedFile().getPath();
        if ( YES_OPTION == showConfirmDialog(UiCore.getMainFrame(), "PriceManagment: " + fileName + " importieren ?", "Import des PriceManagments II", YES_NO_OPTION, QUESTION_MESSAGE) ) {
            new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    FileUtil.checkIfExcelFile(dialog.getSelectedFile());
                    FileJacket inFile = new FileJacket("in", ".xls", new File(fileName));
                    Dl.remote().lookup(Importer.class).fromXls(inFile, Dl.local().lookup(Guardian.class).getUsername());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Ui.handle(ex);
                    }
                }
            }.execute();
        }
    }
}
