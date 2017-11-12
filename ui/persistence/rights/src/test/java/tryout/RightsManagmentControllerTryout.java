package tryout;

import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Test;

import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.action.RightsManagmentAction;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.Client;

import tryout.stub.RightsAgentStub;

/**
 *
 * @author Bastian Venz
 */
public class RightsManagmentControllerTryout {

    boolean complete = false;

    @Test
    public void testSomeMethod() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Client.addSampleStub(RightsAgent.class, new RightsAgentStub());

        UiCore.startSwing(() -> {
            JButton close = new JButton("Schliessen");
            close.addActionListener(l -> latch.countDown());

            JPanel main = new JPanel();
            main.add(new JButton(new RightsManagmentAction()));
            main.add(close);
            return main;
        });

        latch.await();
    }

}
