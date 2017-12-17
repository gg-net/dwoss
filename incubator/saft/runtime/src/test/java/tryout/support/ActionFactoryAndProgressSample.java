package tryout.support;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.progress.ProgressObserver;
import eu.ggnet.saft.core.ActionFactory;
import eu.ggnet.saft.core.Client;

import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class ActionFactoryAndProgressSample implements ActionFactory {

    private final ProgressObserverStub progressObserver = new ProgressObserverStub();

    public ActionFactoryAndProgressSample() {
        Client.addSampleStub(ProgressObserver.class, progressObserver);
    }

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("System", new AbstractAction("Klick mich") {
                    {
                        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("/kua_small.png")));
                        putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/kua_large.png")));
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Ich wurde geklickt");
                    }
                }, true),
                new MetaAction("System", new AbstractAction("Saft Progress 4s") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Ui.exec(() -> {
                            Ui.progress().call(() -> {
                                Thread.sleep(4000);
                                return null;
                            });
                        });
                    }
                }),
                new MetaAction("System", new BackgroundProgressAction(progressObserver))
        );
    }
}
