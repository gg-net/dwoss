package tryout;

import java.util.EnumSet;

import javax.swing.JLabel;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.report.ui.cap.aux.RevenueReportSelectionView;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.Client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author oliver.guenther
 */
public class RevenueReportSelectionViewTryout {

    public static void main(String[] args) {
        MandatorSupporter mandatorSupporterMock = mock(MandatorSupporter.class);
        when(mandatorSupporterMock.loadContractors()).thenReturn(new Contractors(
                EnumSet.of(TradeName.FUJITSU),
                EnumSet.of(TradeName.FUJITSU)
        )); // Not yet implemented
        Client.addSampleStub(MandatorSupporter.class, mandatorSupporterMock);

        UiCore.startSwing(() -> new JLabel("Main Applikation"));

        Ui.fx().eval(() -> new RevenueReportSelectionView()).ifPresent(System.out::println);
    }
}
