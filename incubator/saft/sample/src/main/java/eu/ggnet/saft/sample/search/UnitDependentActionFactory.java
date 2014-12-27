/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.saft.sample.search;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.all.DescriptiveConsumer;
import eu.ggnet.saft.core.all.DescriptiveConsumerFactory;

/**
 *
 * @author oliver.guenther
 */
public class UnitDependentActionFactory implements DescriptiveConsumerFactory<MicroUnit> {

    @Override
    public List<DescriptiveConsumer<MicroUnit>> of(MicroUnit t) {
        if ( t.uniqueUnitId != 1 ) return new ArrayList<>();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnitDependentActionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Arrays.asList(new DescriptiveConsumer("Umfuhr in Lager XYZ", x -> Alert.show("Umfuhr ausgeführt")));
    }

}
