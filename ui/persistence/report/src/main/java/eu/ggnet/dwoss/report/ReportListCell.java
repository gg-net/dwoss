package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.report.entity.Report;

import javafx.scene.control.*;
import javafx.util.Callback;

/**
 *
 * @author pascal.perau
 */
public class ReportListCell extends ListCell<Report> {

    public static class Factory implements Callback<ListView<Report>, ListCell<Report>> {

        @Override
        public ListCell<Report> call(ListView<Report> p) {
            return new ReportListCell();
        }
    }

    @Override
    protected void updateItem(Report item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setText("");
        } else {
            setText(item.getName());
            setTooltip(new Tooltip(item.getComment()));
        }
    }
}
