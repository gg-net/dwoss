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
package eu.ggnet.dwoss.redtapext.ui.cap;


import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.redtapext.ui.cao.document.position.PositionViewAction;
import eu.ggnet.saft.experimental.ops.ActionFactory;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class RedTapeActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Kunden und Aufträge", new RedTapeAction(), true),
                new MetaAction("Kunden und Aufträge", new DossiersByStatusAction()),
                new MetaAction("Kunden und Aufträge", new ShowUnitViewAction()),
                new MetaAction("Artikelstamm", new SalesProductAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new ExportDossierToXlsAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new CreditMemoReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new OptimizedCreditMemoReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new DebitorsReportAction()),
                new MetaAction("Geschäftsführung", "Allgemeine Reporte", new DirectDebitReportAction()),
                new MetaAction("Geschäftsführung", "Abschluss Reporte", new LastWeekCloseAction()),
                new MetaAction("Geschäftsführung", new SageExportAction())
        );
    }

    @Override
    public List<Consumer<?>> createDependentActions() {
        return Arrays.asList(new PositionViewAction());
    }

}
