package eu.ggnet.dwoss.receipt;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

import eu.ggnet.dwoss.receipt.product.CpuManagementAction;
import eu.ggnet.dwoss.receipt.product.UpdateProductAction;
import eu.ggnet.dwoss.receipt.product.GpuManagementAction;
import eu.ggnet.dwoss.receipt.product.SpecListAction;
import eu.ggnet.dwoss.receipt.reporting.AuditReportByRangeAction;
import eu.ggnet.dwoss.receipt.reporting.AuditReportOnRollInAction;
import eu.ggnet.dwoss.receipt.reporting.ReportRefurbishmentAction;
import eu.ggnet.dwoss.receipt.unit.AddCommentAction;
import eu.ggnet.dwoss.receipt.unit.EditUnitAction;

/**
 * Action Factory for Receipt.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class ReceiptActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Lager/Logistik", null),
                new MetaAction("Lager/Logistik", new OpenShipmentAction()),
                new MetaAction("Lager/Logistik", new EditUnitAction()),
                new MetaAction("Lager/Logistik", new ScrapUnitAction()),
                new MetaAction("Lager/Logistik", new DeleteUnitAction()),
                new MetaAction("Lager/Logistik", null),
                new MetaAction("Lager/Logistik", new RollInPreparedTransactionsAction()),
                new MetaAction("Lager/Logistik", new AuditReportByRangeAction()),
                new MetaAction("Lager/Logistik", new AuditReportOnRollInAction()),
                new MetaAction("Artikelstamm", new UpdateProductAction()),
                new MetaAction("Artikelstamm", new CpuManagementAction()),
                new MetaAction("Artikelstamm", new GpuManagementAction()),
                new MetaAction("Artikelstamm", new SpecListAction()),
                new MetaAction("Artikelstamm", new AddCommentAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new ReportRefurbishmentAction()));

    }
}
