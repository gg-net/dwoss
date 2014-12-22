package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.api.CallableA1;
import eu.ggnet.saft.sample.support.DocumentAdressUpdateView;
import eu.ggnet.saft.sample.support.MainPanel;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

/**
 * Opens a Swing Panel as Popup Dialog blocking the hole application and on Ok calculates an async result.
 *
 * @author oliver.guenther
 */
public class AsyncRunThenPopUp {

    private static class HardWorker {

        public static <T> T work2s(String worktype, T t) throws InterruptedException {
            System.out.print("Doing 2 sec " + worktype + " work ... ");
            Thread.sleep(2000);
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

        Ui.exec(Ui
                .call(() -> HardWorker.work2s("per", "Eine leere Adresse"))
                .choiceSwing(DocumentAdressUpdateView.class)
                .onOk((t) -> HardWorker.work2s("middle", t.getAddress()))
                .choiceSwing(DocumentAdressUpdateView.class)
                .onOk((t) -> HardWorker.work2s("post", t.getAddress()))
        );
    }

    public static void longer() {
        // A JAva 7 View.
        Ui.exec(Ui.call(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "Hallo";
            }

        })
                .choiceSwing(DocumentAdressUpdateView.class)
                .onOk(new CallableA1<DocumentAdressUpdateView, Integer>() {

                    @Override
                    public Integer call(DocumentAdressUpdateView t) throws Exception {
                        return 1;
                    }
                })
        );

    }

}
