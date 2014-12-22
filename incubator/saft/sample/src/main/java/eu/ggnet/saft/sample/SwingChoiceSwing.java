package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.support.DocumentAdressUpdateView;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Opens a Swing Panel as Popup Dialog blocking the hole application.
 *
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
public class SwingChoiceSwing {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        String adress = "Hans Mustermann\nMusterstrasse 22\n12345 Musterhausen";
        // Swing Panel in Swing Dialog
        Ui.exec(Ui.choiceSwing(DocumentAdressUpdateView.class) // Needs to be in the UI Thread, should block all
                .onOk(v -> {
                    System.out.println(v.getAddress());
                    return null;
                }) // Hint: in the implementations, most of the time, we have some result. Than the short form is possible.
        );
    }
}
