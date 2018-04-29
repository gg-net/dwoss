package tryout;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.dwoss.rights.ui.cap.RightsManagmentAction;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.UiCore;

import tryout.stub.RightsAgentStub;

/**
 *
 * @author Bastian Venz
 */
public class RightsManagmentControllerTryout {

    public static void main(String[] args) {
        Dl.remote().add(RightsAgent.class, new RightsAgentStub());

        UiCore.startSwing(() -> {
            JPanel main = new JPanel();
            main.add(new JButton(new RightsManagmentAction()));
            return main;
        });

    }

}
