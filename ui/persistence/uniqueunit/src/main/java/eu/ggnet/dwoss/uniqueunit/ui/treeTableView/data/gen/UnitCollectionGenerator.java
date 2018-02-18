/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.treeTableView.data.gen;

import java.util.*;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UnitCollection;

/**
 *
 * @author lucas.huelsen
 */
public class UnitCollectionGenerator {

    private final Random rand = new Random();

    private final List<String> names = new ArrayList<>(Arrays.asList("Neuware", "leichte Gebrauchsspuren", "Für Bastler", "Limited Edition", "test"));

    private final List<TradeName> tradeName = new ArrayList<>(Arrays.asList(TradeName.values()));

    private final List<ProductGroup> productGroup = new ArrayList<>(Arrays.asList(ProductGroup.values()));

    private final UniqueUnitGenerator uuGen = new UniqueUnitGenerator();

    public List<UnitCollection> generateUnitCollections() {

        List<UnitCollection> collections = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {

            UnitCollection uc = new UnitCollection();
            uc.setNameExtension(names.get(i));
            uc.setSalesChannel(SalesChannel.RETAILER);
            uc.setDescriptionExtension("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
            uc.setPrice(PriceType.SALE, 350.00, "comment");

            for (int j = 0; j < 3; j++) {
                uc.getUnits().add(uuGen.makeUniqueUnit(tradeName.get(rand.nextInt(tradeName.size())), productGroup.get(rand.nextInt(productGroup.size()))));
            }

            collections.add(uc);
        }
        return collections;
    }

}
