package tryout;

import java.awt.Dimension;

import javax.swing.*;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ui.categoryProduct.CategoryProductListController;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.Guardian;

import tryout.stub.GuardianStub;
import tryout.stub.UniqueUnitAgentStub;

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

        Dl.remote().add(UniqueUnitAgent.class, new UniqueUnitAgentStub());
        Dl.local().add(Guardian.class, new GuardianStub());

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
