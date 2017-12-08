package tryout;

import tryout.stub.UniqueUnitAgentStub;

import java.awt.Dimension;

import javax.swing.*;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.uniqueunit.ui.product.ProductListController;

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
    public static void main(String[] args) throws InterruptedException {

        Client.addSampleStub(UniqueUnitAgent.class, new UniqueUnitAgentStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.fxml().show(ProductListController.class);

        });

        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setDragEnabled(true);
        textField.setTransferHandler(new TransferHandler("dw/product") {

        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(textField);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
