package eu.ggnet.dwoss.receipt.shipment;

import java.util.Arrays;

import javax.swing.UIManager;

import org.junit.Test;

import eu.ggnet.dwoss.common.ui.AbstractGuardian;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.core.auth.Guardian;

import eu.ggnet.dwoss.stock.upi.StockUpi;
import eu.ggnet.dwoss.stock.upi.impl.StockUpiImpl;

/**
 *
 * @author oliver.guenther
 */
public class ShipmentDialogTryout {

    @Test
    public void testShipment() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Dl.local().add(Guardian.class, new AbstractGuardian() {

            {
                setRights(new Operator("Testuser", 0, Arrays.asList(AtomicRight.values())));
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });
        StockUpiImpl su = new StockUpiImpl();
        su.setActiveStock(new PicoStock(0, "Demostock"));
        Dl.local().add(StockUpi.class, su);
        Dl.remote().add(StockAgent.class, new StockAgentStub());

        ShipmentController controller = new ShipmentController();
        ShipmentDialog dialog = new ShipmentDialog(new javax.swing.JFrame(), controller);
        dialog.setVisible(true);
    }

}
