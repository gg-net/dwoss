package tryout;

import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ui.product.ProductListController;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.core.auth.Guardian;

import tryout.stub.UniqueUnitAgentStub;

/**
 * Test the ListView with Sample data.
 *
 * @author lucas.huelsen
 */
public class ProductListTryout {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Dl.remote().add(UniqueUnitAgent.class, new UniqueUnitAgentStub());
        Dl.local().add(Guardian.class, new AbstractGuardian() {
            {
                setRights(new Operator("hans", 123, Arrays.asList(AtomicRight.values())));
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });
        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().show(ProductListController.class);
            });
        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
