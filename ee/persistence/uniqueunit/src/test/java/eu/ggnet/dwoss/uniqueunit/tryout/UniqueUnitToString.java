/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.tryout;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitToString {

    public static void main(String[] args) {
        UniqueUnit uu = new UniqueUnit();
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "12345");
        uu.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "54321");

        System.out.println(uu);
        uu.addHistory("Blabla");
        System.out.println(uu.getHistory());
    }

}
