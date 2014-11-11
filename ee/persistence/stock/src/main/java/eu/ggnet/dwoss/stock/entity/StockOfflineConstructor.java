/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.stock.entity;

import javax.swing.JOptionPane;

/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class StockOfflineConstructor {

    private static StockOfflineConstructor instance;

    public int counter;

    private StockOfflineConstructor() {
        String messages= "Warning: The " + this.getClass().getSimpleName() + " is in use.\n In a Prodcutive Environment, this will very probably cause Datalose!";
        JOptionPane.showMessageDialog(null, messages,"Warning, OfflineConstructor in use",JOptionPane.WARNING_MESSAGE);
        counter = 0;
    }

    public static StockOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new StockOfflineConstructor();
        }
        return instance;
    }

    public StockUnit newStockUnit(String unitId, String name) {
        StockUnit stockUnit = new StockUnit(counter++);
        stockUnit.setName(name);
        stockUnit.setRefurbishId(unitId);
        return stockUnit;
    }

}
