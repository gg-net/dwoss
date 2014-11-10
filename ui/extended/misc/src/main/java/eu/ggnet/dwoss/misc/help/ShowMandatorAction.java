package eu.ggnet.dwoss.misc.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.util.MetawidgetConfig;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class ShowMandatorAction extends AbstractAction {

    public ShowMandatorAction() {
        super("Aktiver Mandant");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(false, 2, TradeName.class);
        mw.setReadOnly(true);
        mw.setToInspect(lookup(MandatorSupporter.class).loadMandator());
        JDialog dialog = new JDialog(lookup(Workspace.class).getMainFrame(), "Aktiver Mandant");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(mw);
        dialog.pack();
        dialog.setLocationRelativeTo(lookup(Workspace.class).getMainFrame());
        dialog.setVisible(true);
    }
}
