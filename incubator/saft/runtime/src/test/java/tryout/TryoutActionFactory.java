package tryout;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

import static javax.swing.Action.SMALL_ICON;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class TryoutActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(new MetaAction("System", new AbstractAction("Klick mich") {
            {
                putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/kua_small.png")));
                putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/kua_large.png")));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Ich wurde geklickt");
            }
        }, true));
    }
}
