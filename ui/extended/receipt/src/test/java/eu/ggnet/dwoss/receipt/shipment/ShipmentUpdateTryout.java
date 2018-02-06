package eu.ggnet.dwoss.receipt.shipment;

import java.util.EnumSet;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.junit.Test;

import eu.ggnet.dwoss.mandator.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.stock.entity.Shipment;
import eu.ggnet.saft.Dl;

/**
 *
 * @author pascal.perau
 */
public class ShipmentUpdateTryout {

    boolean complete = false;

    @Test
    public void tryout() throws InterruptedException {

        Dl.remote().add(Mandators.class, new Mandators() {

            @Override
            public Mandator loadMandator() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ReceiptCustomers loadReceiptCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SpecialSystemCustomers loadSystemCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Contractors loadContractors() {
                return new Contractors(EnumSet.allOf(TradeName.class), EnumSet.allOf(TradeName.class));
            }

            @Override
            public PostLedger loadPostLedger() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

        new JFXPanel();    // To start the platform
        Shipment s = new Shipment();

        Platform.runLater(() -> {
            ShipmentUpdateStage stage = new ShipmentUpdateStage(s);
            stage.showAndWait();
            System.out.println(stage.getShipment());
            complete = true;
        });

        while (!complete) {
            Thread.sleep(500);
        }
    }

}
