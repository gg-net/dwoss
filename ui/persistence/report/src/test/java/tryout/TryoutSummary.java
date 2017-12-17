package tryout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jdesktop.beansbinding.AutoBinding;
import org.junit.Test;
import org.metawidget.swing.SwingMetawidget;

import eu.ggnet.dwoss.report.ui.returns.Summary;
import eu.ggnet.dwoss.util.MetawidgetConfig;

/**
 *
 * @author oliver.guenther
 */
public class TryoutSummary {

    @Test
    public void show() throws InterruptedException {

        Summary s = new Summary();
        s.setMargin("100,00 €");
        s.setMarginPercentage("10,3%");
        s.setPrice("1000,00 €");
        s.setPurchasePrice("900,00 €");
        s.setReferencePrice("2000,00 €");
        s.setReferencePricePercentage("20%");

        SwingMetawidget mw = MetawidgetConfig.builder().updateStrategy(AutoBinding.UpdateStrategy.READ_WRITE).numberOfColums(6).readOnly(true).build();
        mw.setToInspect(s);
        JFrame dialog = new JFrame("Summary");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(mw);
        dialog.pack();
        dialog.setSize(dialog.getSize().width, dialog.getSize().height + 50);
        dialog.setLocationByPlatform(true);
        dialog.setVisible(true);
        Thread.sleep(10000);
        s.setPrice("AAAAAAAAAAAAa");
        s.setReferencePrice("BBBBBBBBBBBBbb");
        System.out.println("Price set");
        Thread.sleep(10000);

    }

}
