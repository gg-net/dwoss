package eu.ggnet.dwoss.rights;

import java.util.Arrays;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.ActionFactory;
import eu.ggnet.saft.core.ActionFactory.MetaAction;

import eu.ggnet.dwoss.rights.action.RightsManagmentAction;

/**
 *
 * @author Bastian Venz
 */
@ServiceProvider(service = ActionFactory.class)
public class RightsActionFactory implements ActionFactory {

    private static final String MENU_NAME = "Rechte";

    @Override
    public List<MetaAction> createMetaActions() {
        return Arrays.asList(
                new MetaAction(MENU_NAME, new RightsManagmentAction())
        );
    }
}
