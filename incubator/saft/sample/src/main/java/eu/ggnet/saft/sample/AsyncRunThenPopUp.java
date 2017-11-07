package eu.ggnet.saft.sample;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import eu.ggnet.saft.api.CallableA1;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.support.*;

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
//        saftClassic();

    }

    public static void saftNew() {
        Ui.exec(() -> {
            Ui.swing().eval(() -> HardWorker.work2s("per", "Eine leere Adresse"), () -> new DocumentAdressUpdateViewOkCanceler())
                    .map(t -> HardWorker.work2s("middle", t))
                    .map(t -> Ui.swing().eval(() -> t, () -> new DocumentAdressUpdateViewOkCanceler()))
                    .ifPresent(t -> HardWorker.work2s("post", t));
        });

    }

    public static void saftClassic() {
        Ui.exec(Ui
                .call(() -> HardWorker.work2s("per", "Eine leere Adresse"))
                .choiceSwing(DocumentAdressUpdateView.class)
                .onOk((t) -> HardWorker.work2s("middle", t.getAddress()))
                .choiceSwing(DocumentAdressUpdateView.class)
                .onOk((t) -> HardWorker.work2s("post", t.getAddress()))
        );
    }

    public static void saftClassicJ7() {
        // A Java 7 View.
        Ui.exec(Ui.call(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "Hallo";
            }

        }).choiceSwing(DocumentAdressUpdateView.class)
                .onOk(new CallableA1<DocumentAdressUpdateView, Integer>() {

                    @Override
                    public Integer call(DocumentAdressUpdateView t) throws Exception {
                        return 1;
                    }
                })
        );

    }

}
