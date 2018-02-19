package eu.ggnet.saft.sample;

import java.awt.EventQueue;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.DocumentAdressUpdateViewOkCanceler;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Opens a Swing Panel as Popup Dialog blocking the hole application and on Ok calculates an async result.
 *
 * @author oliver.guenther
 */
public class AsyncRunThenPopUp {

    private static class HardWorker {

        public static <T> T work2s(String worktype, T t) {
            System.out.print("Doing 2 sec " + worktype + " work ... ");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            System.out.println("done");
            return t;
        }

    }

    public static void main(String[] args) {
        final MainPanel panel = new MainPanel();
        UiCore.startSwing(() -> panel);
        UiCore.backgroundActivityProperty().addListener((o, ov, nv) -> {
            EventQueue.invokeLater(() -> {
                panel.getProgressBar().setIndeterminate(nv);
            });
        });

        saftNew();
    }

    public static void saftNew() {
        Ui.exec(() -> {
            Ui.build().swing().eval(() -> HardWorker.work2s("per", "Eine leere Adresse"), () -> new DocumentAdressUpdateViewOkCanceler()).opt()
                    .map(Ui.progress().wrap(t -> HardWorker.work2s("middle", t)))
                    .map(t -> Ui.build().swing().eval(() -> t, () -> new DocumentAdressUpdateViewOkCanceler()))
                    .ifPresent(t -> HardWorker.work2s("post", t));
        });

    }

}
