package eu.ggnet.dwoss.customer.action;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;
import eu.ggnet.saft.core.ActionFactory.MetaAction;

/**
 *
 * <p>
 * @author oliver.guenther
 */
@ServiceProvider(service = ActionFactory.class)
public class CustomerActionFactory implements ActionFactory {

    private static final String MENU_NAME = "System";

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction(MENU_NAME, "Datenbank", new RecreateSearchIndex())
        );
    }

}
