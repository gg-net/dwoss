package eu.ggnet.dwoss.rights;

import eu.ggnet.dwoss.rights.entity.Operator;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Bastian Venz
 */
public class OperatorListCell extends ListCell<Operator> {

    public static class Factory implements Callback<ListView<Operator>, ListCell<Operator>> {

        @Override
        public ListCell<Operator> call(ListView<Operator> p) {
            return new OperatorListCell(p);
        }
    }

    public OperatorListCell(ListView<Operator> listView) {

    }

    @Override
    protected void updateItem(Operator item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }
        setText(item.getUsername());
    }
}
