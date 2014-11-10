package eu.ggnet.dwoss.stock;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;

import eu.ggnet.dwoss.stock.action.OpenCommissioningManager;
import eu.ggnet.dwoss.stock.action.OpenStockTransactionManager;
import eu.ggnet.dwoss.stock.action.PrepareSimpleTransferAction;
import eu.ggnet.dwoss.stock.action.RemoveUnitFromTransactionAction;

/**
 * ActionFactory for Stock.
 * <p/>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class StockActionFactory implements ActionFactory {

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction("Lager/Logistik", null),
                new MetaAction("Lager/Logistik", new PrepareSimpleTransferAction()),
                new MetaAction("Lager/Logistik", new RemoveUnitFromTransactionAction()),
                new MetaAction("Lager/Logistik", new OpenStockTransactionManager()),
                new MetaAction("Lager/Logistik", new OpenCommissioningManager()));
    }
}
