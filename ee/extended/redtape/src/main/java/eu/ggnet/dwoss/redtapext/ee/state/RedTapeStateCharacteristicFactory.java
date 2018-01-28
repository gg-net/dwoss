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
package eu.ggnet.dwoss.redtapext.ee.state;

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
