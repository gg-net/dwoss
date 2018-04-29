/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout;

import javax.swing.UIManager;

import eu.ggnet.dwoss.common.api.values.ReceiptOperation;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor.EditableUnit;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentInclusionViewCask;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.RemoteLookupStub;
import eu.ggnet.dwoss.stock.ee.entity.Shipment;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.RemoteLookup;

/**
 *
 * @author oliver.guenther
 */
public class ShipmentInclusionViewTryout {

    public static void main(String[] args) throws Exception {
        Dl.local().add(RemoteLookup.class, new RemoteLookupStub());
        Dl.remote().add(UnitProcessor.class, new UnitProcessor() {
            @Override
            public void receipt(UniqueUnit recieptUnit, Product product, Shipment shipment, StockTransaction transaction, ReceiptOperation operation, String operationComment, String arranger) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void update(UniqueUnit uniqueUnit, Product product, ReceiptOperation updateOperation, String operationComment, String arranger) throws IllegalArgumentException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public EditableUnit findEditableUnit(String refurbishedIdOrSerial) throws UserInfoException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public UniqueUnit transfer(UniqueUnit uniqueUnit, int stockId, String arranger) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Shipment shipment = new Shipment();
        shipment.setShipmentId("Lenovo-0021");
        shipment.setContractor(TradeName.LENOVO);
        ShipmentInclusionViewCask sid = new ShipmentInclusionViewCask(null, shipment, null);
        sid.setLocationRelativeTo(null);
        sid.setVisible(true);
        System.out.println("Is Aborted: " + sid.isInclusionAbort());
        System.out.println("Is Closed: " + sid.isInclusionClosed());
        System.exit(0);
    }

}
