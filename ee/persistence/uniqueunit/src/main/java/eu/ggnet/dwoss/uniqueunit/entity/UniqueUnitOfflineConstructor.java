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
package eu.ggnet.dwoss.uniqueunit.entity;

import java.util.Date;

import javax.swing.JOptionPane;

/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class UniqueUnitOfflineConstructor {

    private static UniqueUnitOfflineConstructor instance;

    public int counter;

    private UniqueUnitOfflineConstructor() {
        String messages= "Warning: The " + this.getClass().getSimpleName() + " is in use.\n In a Prodcutive Environment, this will very probably cause Datalose!";
        JOptionPane.showMessageDialog(null, messages,"Warning, OfflineConstructor in use",JOptionPane.WARNING_MESSAGE);
        counter = 0;
    }

    public static UniqueUnitOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new UniqueUnitOfflineConstructor();
        }
        return instance;
    }

    public UniqueUnit newUniqueUnit(Date mfgDate, UniqueUnit.Condition condition) {
        UniqueUnit uu = new UniqueUnit(counter++);
        uu.setMfgDate(mfgDate);
        uu.setCondition(condition);
        return uu;
    }

    public Product newProduct() {
        return new Product();
    }

}
