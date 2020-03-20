package tryout.support;

import java.awt.EventQueue;
import java.util.concurrent.CountDownLatch;

import javafx.embed.swing.JFXPanel;

import org.junit.Test;

import eu.ggnet.dwoss.assembly.remote.client.SwingClient;

/**
 *
 * @author oliver.guenther
 */
public class ClientTryout {

    @Test
    public void tryout() throws InterruptedException {
        JFXPanel p = new JFXPanel();
        CountDownLatch cdl = new CountDownLatch(1);
        final SwingClient ac = new SwingClient() {
            @Override
            protected void close() {
                System.out.println("closing");
                cdl.countDown();
            }
        };
        ac.init();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ac.show("Tryout", null);

            }
        });
        cdl.await();
    }
}
