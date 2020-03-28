/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.receipt.ui.cap;


import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.widget.ops.ActionFactory;
import eu.ggnet.dwoss.receipt.ui.product.SpecListAction;

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
