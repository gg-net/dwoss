package tryout;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ui.product.ProductListController;
import eu.ggnet.saft.*;

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

        Client.addSampleStub(UniqueUnitAgent.class, new UniqueUnitAgentStub());

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
