/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Modality;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ui.*;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView.In;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.receipt.ui.StockDto;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.AlertType;

/**
 *
 * @author mirko.schulze
 */
public class EditUnitMenuItem extends MenuItem {

    @Inject
    private RemoteDl remote;

    @Inject
    private Saft saft;

    @Inject
    private Guardian guardian;

    public EditUnitMenuItem() {
        super("Gerät bearbeiten");
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> {
            //saft dialog builder
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Eingabe");
            dialog.setHeaderText("Bitte SopoNr/Seriennummer eingeben: ");
            dialog.showAndWait().ifPresent(id -> {
                try {
                    editUnit(id);
                } catch (UserInfoException ex) {
                    saft.handle(ex);
                }
            });
        });
    }

    /**
     * Starts the Ui to edit an existing Unit.
     * <p/>
     * @param refurbishedIdOrSerial the refurbishId or a serial
     * @throws UserInfoException if the unit may not be edited.
     */
    public void editUnit(String refurbishedIdOrSerial) throws UserInfoException {
        if ( refurbishedIdOrSerial == null || refurbishedIdOrSerial.trim().equals("") ) return;
        refurbishedIdOrSerial = refurbishedIdOrSerial.trim().toUpperCase();
        UnitProcessor.EditableUnit eu = remote.lookup(UnitProcessor.class).findEditableUnit(refurbishedIdOrSerial);
        if ( eu.operation == ReceiptOperation.IN_SALE ) {
            saft.build().alert().message("Achtung, dieses Gerät ist in einem Kundenauftrag, ändern nicht empfohlen.").show(AlertType.WARNING);
        } else if ( eu.operation != ReceiptOperation.SALEABLE ) {
            saft.build().alert().message("Gerät ist in Operation : " + eu.operation).show(AlertType.INFO);
        }

        final UniqueUnit uu = (eu.stockUnit == null
                ? eu.uniqueUnit
                : optionalChangeStock(eu.uniqueUnit, eu.stockUnit, Dl.local().lookup(ActiveStock.class).getActiveStock(), guardian.getUsername()));

        saft.build().swing().eval(() -> new In.Edit(uu, eu.operation, eu.partNo), UnitView.class).cf()
                .thenAccept(result -> {
                    remote.lookup(UnitProcessor.class).update(
                            result.uniqueUnit(),
                            result.product(),
                            result.receiptOperation(),
                            result.comment(),
                            guardian.getUsername()
                    );
                })
                .handle(saft.handler());
    }

    private UniqueUnit optionalChangeStock(UniqueUnit uniqueUnit, StockUnit stockUnit, PicoStock localStock, String account) {
        if ( !stockUnit.isInStock() ) return uniqueUnit;
        if ( localStock.id == stockUnit.getStock().getId() ) return uniqueUnit;
        if ( stockUnit.isInTransaction() ) {
            saft.build().alert().message("Achtung, Gerät ist nicht auf " + localStock.shortDescription + ",")
                    .nl("aber Gerät ist auch auf einer Transaktion.").nl("Automatische Lageränderung nicht möglich !").show(AlertType.WARNING);
            return uniqueUnit;
        }
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Standortabweichung\nGerät steht nicht auf " + localStock.shortDescription + ", welches als Standort angegeben ist.");
        alert.setContentText("Gerätestandort ändern ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        if ( alert.showAndWait().get() == ButtonType.OK ) {
            List<PicoStock> stocks = new ArrayList<>();
            remote.lookup(StockAgent.class).findAll(Stock.class).forEach(s -> stocks.add(s.toPicoStock()));

            CompletableFuture<Void> thenAccept = saft.build()
                    .fxml()
                    .eval(() -> new StockDto(stocks, localStock), StockController.class)
                    .cf()
                    .thenAccept(s -> remote.lookup(UnitProcessor.class).transfer(uniqueUnit, s.id, account));
            thenAccept.join();
            return uniqueUnit;
        }
        return uniqueUnit;
    }

}
