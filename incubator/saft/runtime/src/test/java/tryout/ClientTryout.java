package tryout;

import java.awt.EventQueue;

import org.junit.Test;

import eu.ggnet.saft.runtime.SwingClient;

/**
 *
 * @author oliver.guenther
 */
public class ClientTryout {

    @Test
    public void tryout() throws InterruptedException {
        final SwingClient ac = new SwingClient() {
            @Override
            protected void close() {
                System.out.println("closing");
            }
        };
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ac.show("Tryout", null);
            }
        });

    }
}
