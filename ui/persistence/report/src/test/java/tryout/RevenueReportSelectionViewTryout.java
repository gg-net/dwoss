package tryout;

import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import java.util.EnumSet;

import javax.swing.JLabel;

import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.report.ui.cap.support.RevenueReportSelectionView;
import eu.ggnet.dwoss.common.api.values.TradeName;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author oliver.guenther
 */
public class RevenueReportSelectionViewTryout {

    public static void main(String[] args) {
        Mandators mandatorSupporterMock = mock(Mandators.class);
        when(mandatorSupporterMock.loadContractors()).thenReturn(new Contractors(
                EnumSet.of(TradeName.FUJITSU),
                EnumSet.of(TradeName.FUJITSU)
        )); // Not yet implemented
        Dl.remote().add(Mandators.class, mandatorSupporterMock);

        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Main Applikation"));
            Ui.build().fx().eval(() -> new RevenueReportSelectionView()).opt().ifPresent(System.out::println);
        });
    }
}
