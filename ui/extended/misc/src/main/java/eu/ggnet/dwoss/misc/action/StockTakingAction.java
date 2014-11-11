/* 
 * Copyright (C) 2014 pascal.perau
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
import java.io.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.misc.op.StockTaking;

import eu.ggnet.dwoss.stock.entity.Stock;

import eu.ggnet.dwoss.util.FileJacket;

import eu.ggnet.dwoss.common.ExceptionUtil;

import static eu.ggnet.saft.core.Client.lookup;
import static javax.swing.JOptionPane.*;

/**
 *
 * @author oliver.guenther
 */
public class StockTakingAction extends AbstractAction {

    private final Stock stock;

    public StockTakingAction(Stock stock) {
        super("Inventur" + (stock == null ? "" : " für " + stock.getName()) + " vervollständigen");
        this.stock = stock;
        putValue(Action.SHORT_DESCRIPTION, "Vervollständigt eine Inventur mit den Informationen aus der Datenbank\n"
                + "Benötigt eine XLS Datei die in der ersten Tabelle in der ersten Spalte die Sonderposten Nummern hat\n"
                + "Die oberste Zeile wird als Überschrift ignoriert.");
    }

    public StockTakingAction() {
        this(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser dialog = new JFileChooser();
        dialog.setFileHidingEnabled(true);
        if ( JFileChooser.APPROVE_OPTION != dialog.showOpenDialog(lookup(Workspace.class).getMainFrame()) ) return;
        final File inFile = dialog.getSelectedFile();
        if ( YES_OPTION != showConfirmDialog(lookup(Workspace.class).getMainFrame(),
                "Inventur" + (stock == null ? "" : " für " + stock.getName()) + " aus der Datei:" + inFile.getPath() + " vervollständigen ?", "Inventur vervollständigen",
                YES_NO_OPTION, QUESTION_MESSAGE) ) return;

        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(StockTaking.class).fullfillDetails(new FileJacket("in", "xls", inFile), (stock == null ? null : stock.getId()));
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(get().toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();

    }
}
