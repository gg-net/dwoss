package eu.ggnet.dwoss.rights;

import eu.ggnet.dwoss.rights.entity.Persona;

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
