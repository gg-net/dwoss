package eu.ggnet.dwoss.report;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.report.entity.ReportLine;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.util.MetawidgetConfig;

/**
 *
 * @author oliver.guenther
 */
public class ReportLineUtil {

    public static void show(Component parent, ReportLine rl) {
        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(true, 2, ProductGroup.class, TradeName.class, SalesChannel.class);
        mw.setReadOnly(true);
        mw.setToInspect(rl);
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Details für Reportline(" + rl.getId() + ")");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(mw);
        dialog.pack();
        dialog.setSize(dialog.getSize().width, dialog.getSize().height + 50);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(parent));
        dialog.setVisible(true);
    }

}
