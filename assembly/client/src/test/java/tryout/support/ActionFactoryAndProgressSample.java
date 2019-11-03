package tryout.support;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.saft.experimental.ops.ActionFactory;

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
        Dl.remote().add(ProgressObserver.class, progressObserver);
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
                        Ui.exec(() -> {
                            Ui.build().alert("Ich wurde geklickt");
                        });
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
