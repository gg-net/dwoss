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
import java.io.File;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javafx.scene.control.Alert;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.TikaUtil;
import eu.ggnet.dwoss.core.widget.saft.Failure;
import eu.ggnet.dwoss.misc.ee.StockTaking;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.core.widget.saft.Reply;
import eu.ggnet.saft.core.Ui;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author oliver.guenther
 */
public class StockTakingAction extends AbstractAction {

    private final PicoStock stock;

    public StockTakingAction(PicoStock stock) {
        super("Inventur" + (stock == null ? "" : " für " + stock.shortDescription) + " vervollständigen");
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
        Ui.exec(() -> {
            Optional<File> inFile = Ui.fileChooser().open().opt();
            if ( !inFile.isPresent() ) return;
            Ui.build().dialog().eval(
                    () -> new Alert(CONFIRMATION, (stock == null ? "" : " für " + stock.shortDescription) + " aus der Datei:" + inFile.get().getPath() + " vervollständigen ?"))
                    .opt()
                    .filter(b -> b == OK)
                    .map(b -> TikaUtil.isExcel(inFile.get()))
                    .filter(Failure::handle)
                    .map(Reply::getPayload)
                    .map(f -> Ui.progress().call(() -> Dl.remote().lookup(StockTaking.class).fullfillDetails(new FileJacket("in", ".xls", f), (stock == null ? null : stock.id))))
                    .ifPresent(f -> Ui.osOpen(f.toTemporaryFile()));
        });

    }
}
