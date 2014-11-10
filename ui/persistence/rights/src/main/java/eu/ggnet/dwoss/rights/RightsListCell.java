package eu.ggnet.dwoss.rights;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Bastian Venz
 */
public class RightsListCell extends ListCell<AtomicRight> {

    public static class Factory implements Callback<ListView<AtomicRight>, ListCell<AtomicRight>> {

        @Override
        public ListCell<AtomicRight> call(ListView<AtomicRight> p) {
            return new RightsListCell();
        }
    }

    @Override
    protected void updateItem(AtomicRight item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setText("");
        } else {
            setText(item.getName());
        }
    }
}
