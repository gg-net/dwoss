package tryout;

import eu.ggnet.dwoss.core.widget.Dl;

import javax.swing.JLabel;

import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.report.ui.cap.support.RevenueReportSelectionView;
import eu.ggnet.saft.core.*;

/**
 *
 * @author oliver.guenther
 */
public class RevenueReportSelectionViewTryout {

    public static void main(String[] args) {
        Dl.remote().add(Mandators.class, new ManadatorsStub());
        Dl.local().add(CachedMandators.class, new ManadatorsStub());

        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Main Applikation"));
            Ui.build().fx().eval(() -> new RevenueReportSelectionView()).opt().ifPresent(System.out::println);
        });
    }
}
