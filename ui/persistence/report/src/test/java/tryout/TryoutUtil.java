package tryout;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Window;

import javax.swing.JFrame;

import eu.ggnet.saft.core.Client;
import eu.ggnet.saft.core.Workspace;

/**
 * Helper Util for Tryout Dialoge.
 * <p>
 * @author oliver.guenther
 */
public class TryoutUtil {

    public static void initAndShow() {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new Label("Close if finished"), BorderLayout.CENTER);
        f.setLocationByPlatform(true);
        f.pack();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        Client.lookup(Workspace.class).setMainFrame(f);
    }

    public static void waitForClose() {
        Window w = Client.lookup(Workspace.class).getMainFrame();
        while (w.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
