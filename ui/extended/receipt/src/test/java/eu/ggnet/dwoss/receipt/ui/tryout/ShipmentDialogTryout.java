package eu.ggnet.dwoss.receipt.ui.tryout;

import java.util.Arrays;

import javax.swing.UIManager;

import eu.ggnet.dwoss.common.ui.AbstractGuardian;
import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentController;
import eu.ggnet.dwoss.receipt.ui.shipment.ShipmentDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.*;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.upi.StockUpi;
import eu.ggnet.dwoss.stock.upi.impl.StockUpiImpl;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;
import eu.ggnet.saft.core.dl.RemoteLookup;

/**
 *
 * @author oliver.guenther
 */
public class ShipmentDialogTryout {

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
        Dl.local().add(StockUpi.class, su);
        Dl.remote().add(StockAgent.class, new StockAgentStub());

        ShipmentController controller = new ShipmentController();
        ShipmentDialog dialog = new ShipmentDialog(new javax.swing.JFrame(), controller);
        dialog.setVisible(true);
    }

}
