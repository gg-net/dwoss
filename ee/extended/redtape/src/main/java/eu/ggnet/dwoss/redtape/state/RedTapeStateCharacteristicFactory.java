package eu.ggnet.dwoss.redtape.state;

import eu.ggnet.statemachine.StateCharacteristic;
import eu.ggnet.statemachine.StateCharacteristicFactory;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeStateCharacteristicFactory implements StateCharacteristicFactory<CustomerDocument> {

    @Override
    public StateCharacteristic<CustomerDocument> characterize(CustomerDocument cd) {
        return new RedTapeStateCharacteristic(cd.getDocument().getType(),
                cd.getDocument().getDossier().getPaymentMethod(),
                cd.getDocument().getConditions(),
                cd.getDocument().getDirective(),
                cd.getCustomerFlags(),
                cd.getDocument().getDossier().isDispatch());
    }
}
