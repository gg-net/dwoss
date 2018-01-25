package tryout;

import tryout.stub.UniqueUnitAgentStub;
import tryout.stub.GuardianStub;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.core.auth.Guardian;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.ggnet.dwoss.uniqueunit.ui.categoryProduct.CategoryProductListController;

/**
 * Test the ListView with Sample data.
 *
 * @author lucas.huelsen
 */
public class CategoryProductListEditTryout {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        Client.addSampleStub(UniqueUnitAgent.class, new UniqueUnitAgentStub());
        Client.addSampleStub(Guardian.class, new GuardianStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {
            Ui.build().fxml().show(CategoryProductListController.class);
        });

        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setDragEnabled(true);

        JPanel p = new JPanel();
        p.add(run);
        p.add(textField);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
