package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Ui;

import java.awt.Dialog;
import java.awt.Label;

import javax.swing.JDialog;

import eu.ggnet.saft.sample.support.DocumentAdressUpdateViewOkCanceler;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Opens a Swing Panel as Popup Dialog blocking the hole application.
 * <p>
 * Shows:
 * <ul>
 * <li>Swing JPanel in UI Chain</li>
 * <li>onOk optional Chain continue</li>
 * <li>@OnOk - try deleting the hole address field</li>
 * <li></li>
 * </ul>
 *
 * @author oliver.guenther
 */
public class SwingPopupSwingParentSwing {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        final JDialog d = new JDialog(SwingCore.mainFrame(), "ExtraDialog", Dialog.ModalityType.MODELESS);
        Label label = new Label("Ein extra Dialog");
        d.getContentPane().add(label);
        d.pack();
        d.setLocation(500, 500);
        d.setVisible(true);

        String adress = "Hans Mustermann\nMusterstrasse 22\n12345 Musterhausen";
        // Swing Panel in Swing Dialog
        Ui.exec(() -> {
            Ui.swing().parent(label).eval(() -> adress, () -> new DocumentAdressUpdateViewOkCanceler()).ifPresent(System.out::println);
        });
    }
}
