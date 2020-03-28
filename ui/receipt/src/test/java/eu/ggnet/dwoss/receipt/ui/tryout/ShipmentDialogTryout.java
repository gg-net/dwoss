package eu.ggnet.dwoss.receipt.ui.tryout;

import java.util.Arrays;

import javax.swing.UIManager;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentController;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.stock.spi.ActiveStock;

/**
 *
 * @author oliver.guenther
 */
public class ShipmentDialogTryout {

    public static class StockUpiImpl implements ActiveStock {

        private PicoStock activeStock;

        @Override
        public PicoStock getActiveStock() {
            return activeStock;
        }

        @Override
        public void setActiveStock(PicoStock activeStock) {
            this.activeStock = activeStock;
        }

    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Dl.local().add(RemoteLookup.class, new RemoteLookupStub());
        Dl.local().add(Guardian.class, new AbstractGuardian() {

            {
                setRights(new Operator("Testuser", 0, Arrays.asList(AtomicRight.values())));
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });
        Dl.remote().add(Mandators.class, new MandatorsStub());

        StockUpiImpl su = new StockUpiImpl();
        su.setActiveStock(new PicoStock(0, "Demostock"));
        Dl.local().add(ActiveStock.class, su);
        Dl.remote().add(StockAgent.class, new StockAgentStub());

        ShipmentController controller = new ShipmentController();
        ShipmentDialog dialog = new ShipmentDialog(new javax.swing.JFrame(), controller);
        dialog.setVisible(true);
    }

}
