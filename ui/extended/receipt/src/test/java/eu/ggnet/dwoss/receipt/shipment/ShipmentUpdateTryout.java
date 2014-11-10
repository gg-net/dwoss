package eu.ggnet.dwoss.receipt.shipment;

import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.receipt.shipment.ShipmentUpdateStage;

import java.util.EnumSet;

import org.junit.Test;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.stock.entity.Shipment;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 *
 * @author pascal.perau
 */
public class ShipmentUpdateTryout {

    boolean complete = false;

    @Test
    public void tryout() throws InterruptedException {

        Client.addSampleStub(MandatorSupporter.class, new MandatorSupporter() {

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
