package eu.ggnet.dwoss.price;

import java.util.*;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

import eu.ggnet.dwoss.mandator.MandatorSupporter;

import eu.ggnet.dwoss.price.imex.ContractorExportAction;
import eu.ggnet.dwoss.price.imex.ContractorImportAction;

import eu.ggnet.dwoss.rules.TradeName;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * ActionFactory for Prices.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class PriceActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        List<MetaAction> actions = new ArrayList<>();

        for (TradeName contractor : lookup(MandatorSupporter.class).loadContractors().all()) {
            actions.add(new MetaAction("Geschäftsführung", "Im-/Export", new ContractorExportAction(contractor)));
            actions.add(new MetaAction("Geschäftsführung", "Im-/Export", new ContractorImportAction(contractor)));
        }

        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceExportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceImportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceBlockerAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new GenerateOnePriceAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceExportImportAction()));
        actions.add(new MetaAction("Geschäftsführung", "Preise", new PriceByInputFileAction()));

        return actions;
    }
}
