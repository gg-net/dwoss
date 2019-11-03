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
package eu.ggnet.dwoss.rights.ui;

import eu.ggnet.dwoss.rights.ee.entity.Persona;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Bastian Venz
 */
public class PersonaListCell extends ListCell<Persona> {

    public static class Factory implements Callback<ListView<Persona>, ListCell<Persona>> {

        @Override
        public ListCell<Persona> call(ListView<Persona> p) {
            return new PersonaListCell();
        }
    }

    @Override
    protected void updateItem(Persona item, boolean empty) {
        super.updateItem(item, empty);
        textProperty().unbind();
        setText("");
        if ( !empty || item != null ) textProperty().bind(item.nameProperty());
    }
}
