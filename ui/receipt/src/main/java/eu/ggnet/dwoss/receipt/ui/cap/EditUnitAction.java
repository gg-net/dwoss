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

import java.awt.event.ActionEvent;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ui.unit.StockController;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView.In;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.UiUtil;
import eu.ggnet.saft.core.ui.AlertType;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

/**
 * MenuItem to allow the Manipulation of an existing {@link UniqueUnit}.
 *
 * @author mirko.schulze
 */
public class EditUnitAction extends AccessableAction {

    @Inject
    private RemoteDl remote;

    @Inject
    private Saft saft;

    @Inject
    private Guardian guardian;

    public EditUnitAction() {
        super(UPDATE_UNIQUE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saft.build().dialog()
                .eval(() -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Eingabe");
                    dialog.setHeaderText("Bitte SopoNr/Seriennummer eingeben: ");
                    return dialog;
                }).cf()
                .thenCompose(s -> editUnit(s))
                .handle(saft.handler());
    }

    /**
     * Starts the Ui to edit an existing Unit.
     * <p>
     * @param refurbishedIdOrSerial the refurbishId or a serial
     * @return
     */
    public CompletableFuture<Void> editUnit(final String refurbishedIdOrSerial) throws CompletionException {
        if ( refurbishedIdOrSerial == null || refurbishedIdOrSerial.trim().equals("") ) return CompletableFuture.completedFuture(null);
        UnitProcessor.EditableUnit eu = UiUtil.exceptionRun(() -> remote.lookup(UnitProcessor.class).findEditableUnit(refurbishedIdOrSerial.trim().toUpperCase()));
        if ( eu.operation == ReceiptOperation.IN_SALE ) {
            saft.build().alert().message("Achtung, dieses Gerät ist in einem Kundenauftrag, ändern nicht empfohlen.").show(AlertType.WARNING);
        } else if ( eu.operation != ReceiptOperation.SALEABLE ) {
            saft.build().alert().message("Gerät ist in Operation : " + eu.operation).show(AlertType.INFO);
        }
        final CompletableFuture<UniqueUnit> uuf = (eu.stockUnit == null
                ? CompletableFuture.completedFuture(eu.uniqueUnit)
                : optionalChangeStock(eu.uniqueUnit, eu.stockUnit, Dl.local().lookup(ActiveStock.class).getActiveStock(), guardian.getUsername()));

        return uuf.thenCompose(uu -> saft.build().swing().eval(() -> new In.Edit(uu, eu.operation, eu.partNo), UnitView.class).cf())
                .thenAccept(result -> {
                    remote.lookup(UnitProcessor.class).update(
                            result.uniqueUnit(),
                            result.product(),
                            result.receiptOperation(),
                            result.comment(),
                            guardian.getUsername()
                    );
                });
    }

    private CompletableFuture<UniqueUnit> optionalChangeStock(UniqueUnit uniqueUnit, StockUnit stockUnit, final PicoStock localStock, String account) {
        if ( !stockUnit.isInStock() ) return CompletableFuture.completedFuture(uniqueUnit);
        if ( localStock.id == stockUnit.getStock().getId() ) return CompletableFuture.completedFuture(uniqueUnit);
        if ( stockUnit.isInTransaction() ) {
            saft.build().alert().message("Achtung, Gerät ist nicht auf " + localStock.shortDescription + ",")
                    .nl("aber Gerät ist auch auf einer Transaktion.").nl("Automatische Lageränderung nicht möglich !").show(AlertType.WARNING);
            return CompletableFuture.completedFuture(uniqueUnit);
        }

        return saft.build().dialog()
                .eval(() -> {
                    Alert fxalert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    fxalert.setHeaderText("Standortabweichung\nGerät steht nicht auf " + localStock.shortDescription + ", welches als Standort angegeben ist.");
                    fxalert.setContentText("Gerätestandort ändern ?");
                    fxalert.initModality(Modality.APPLICATION_MODAL);
                    fxalert.getButtonTypes().clear();
                    fxalert.getButtonTypes().addAll(YES, NO);
                    return fxalert;
                }).cf() // Double No case. Think
                .thenApply(bt -> remote.lookup(StockAgent.class).findAll(Stock.class).stream().map(Stock::toPicoStock).collect(Collectors.toList()))
                .thenCompose(picoStocks -> saft.build().fxml().eval(() -> new StockController.In(picoStocks, localStock), StockController.class).cf())
                .thenApply(s -> remote.lookup(UnitProcessor.class).transfer(uniqueUnit, s.id, account))
                .handle((UniqueUnit uu, Throwable ex) -> {
                    if ( ex != null ) {
                        if ( ex instanceof CancellationException || ex.getCause() instanceof CancellationException ) return uniqueUnit; // NO Case, return original unique unit.
                        else if ( ex instanceof RuntimeException ) throw (RuntimeException)ex; // Saftynet
                        else throw new RuntimeException(ex); // Saftynet, Impossible to reach
                    } else if ( uu != null ) {
                        return uu;
                    }
                    throw new IllegalStateException("Impossible Case: CompletableFuture.handle(): uu and ex are null"); // Saftynet, Impossible to reach
                });

    }

}
