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
package eu.ggnet.dwoss.receipt.ui.cap;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ui.StockDialog;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView;
import eu.ggnet.dwoss.receipt.ui.unit.UnitView.In;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.stock.spi.ActiveStock;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.UiCore;

import static eu.ggnet.dwoss.rights.api.AtomicRight.UPDATE_UNIQUE_UNIT;

/**
 * Action to allow the Manipulation of an existing Unit.
 * <p/>
 * @author oliver.guenther
 */
public class EditUnitAction extends AccessableAction {

    @Inject
    private RemoteDl remote;

    @Inject
    private Saft saft;

    @Inject
    private Guardian guardian;

    /**
     * Default Constructor.
     */
    public EditUnitAction() {
        super(UPDATE_UNIQUE_UNIT);
    }

    /**
     * Action to allow the Manipulation of an existing Unit.
     * <p/>
     * @author oliver.guenther
     * @param e the event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        saft.exec(() -> {
            String refurbishedId = JOptionPane.showInputDialog(UiCore.getMainFrame(), "Bitte SopoNr/Seriennummer eingeben:");
            try {
                editUnit(refurbishedId);
            } catch (UserInfoException ex) {
                saft.handle(ex);
            }
        });
    }

    /**
     * Starts the Ui to edit an existing Unit.
     * <p/>
     * @param refurbishedIdOrSerial the refurbishId or a serial
     * @throws UserInfoException if the unit may not be edited.
     */
    // Is public, so it can be used in a tryout.
    public void editUnit(String refurbishedIdOrSerial) throws UserInfoException {
        Window parent = UiCore.getMainFrame();
        if ( refurbishedIdOrSerial == null || refurbishedIdOrSerial.trim().equals("") ) return;
        refurbishedIdOrSerial = refurbishedIdOrSerial.trim().toUpperCase();
        UnitProcessor.EditableUnit eu = remote.lookup(UnitProcessor.class).findEditableUnit(refurbishedIdOrSerial);
        if ( eu.operation == ReceiptOperation.IN_SALE ) {
            JOptionPane.showMessageDialog(parent, "Achtung, dieses Gerät ist in einem Kundenauftrag, ändern nicht empfohlen.");
        } else if ( eu.operation != ReceiptOperation.SALEABLE ) {
            JOptionPane.showMessageDialog(parent, "Gerät ist in Operation : " + eu.operation);
        }

        final UniqueUnit uu = (eu.stockUnit == null
                ? eu.uniqueUnit
                : optionalChangeStock(eu.uniqueUnit, eu.stockUnit, Dl.local().lookup(ActiveStock.class).getActiveStock(), parent, guardian.getUsername()));

        saft.build().parent(parent).swing().eval(() -> new In.Edit(uu, eu.operation, eu.partNo), UnitView.class).cf()
                .thenAccept(result -> {
                    remote.lookup(UnitProcessor.class).update(
                            result.uniqueUnit(),
                            result.product(),
                            result.receiptOperation(),
                            result.comment(),
                            guardian.getUsername()
                    );
                })
                .handle(saft.handler(parent));

    }

    private UniqueUnit optionalChangeStock(UniqueUnit uniqueUnit, StockUnit stockUnit, PicoStock localStock, Window parent, String account) {
        if ( !stockUnit.isInStock() ) return uniqueUnit;
        if ( localStock.id == stockUnit.getStock().getId() ) return uniqueUnit;
        if ( stockUnit.isInTransaction() ) {
            JOptionPane.showMessageDialog(parent,
                    "Achtung, Gerät ist nicht auf " + localStock.shortDescription + ",\n"
                    + "aber Gerät ist auch auf einer Transaktion.\n"
                    + "Automatische Lageränderung nicht möglich !");
            return uniqueUnit;
        }
        int option = JOptionPane.showConfirmDialog(parent,
                "Gerät steht nicht auf " + localStock.shortDescription + ", welches als Standort angegeben ist. Gerätestandort ändern ?",
                "Standortabweichung", JOptionPane.YES_NO_OPTION);
        if ( option == JOptionPane.YES_OPTION ) {
            StockDialog dialog = new StockDialog(parent, Dl.remote().lookup(StockAgent.class).findAll(Stock.class).toArray(new Stock[0]));
            dialog.setSelection(localStock);
            dialog.setVisible(true);
            if ( dialog.isOk() ) return remote.lookup(UnitProcessor.class).transfer(uniqueUnit, dialog.getSelection().getId(), account);
        }
        return uniqueUnit;
    }
}
