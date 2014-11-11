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
