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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.awt.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.redtapext.ee.sage.SageExporter;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.auth.AccessableAction;

import lombok.AllArgsConstructor;

import java.time.*;
import java.util.Date;

import static eu.ggnet.dwoss.rights.api.AtomicRight.EXPORT_DOCUMENTS_FOR_SAGE_IN_XML;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Action to create the GsOfficeXml.
 *
 * @author pascal.perau
 */
public class SageExportAction extends AccessableAction {

    public SageExportAction() {
        super(EXPORT_DOCUMENTS_FOR_SAGE_IN_XML);
    }

    @AllArgsConstructor
    public static class RangeOfDates {

        public LocalDate start;

        public LocalDate end;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().title("Bitte Zeitraum angeben").dialog().eval(() -> {
            Dialog<RangeOfDates> dialog = new Dialog<>();
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker startDate = new DatePicker();
            DatePicker endDate = new DatePicker();

            grid.add(new Label("Bitte Zeitraum angeben: "), 0, 0);
            grid.add(startDate, 1, 0);
            grid.add(endDate, 1, 1);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(OK, CANCEL);

            RangeOfDates result = new RangeOfDates(startDate.getValue(), endDate.getValue());
            dialog.setResult(result);
            return dialog;
        }).ifPresent(r -> {
           Ui.progress().title("Sage Export")
                    .call(() -> Dl.remote().lookup(SageExporter.class).toXml(Date.from(r.start.atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(r.end.atStartOfDay(ZoneId.systemDefault()).toInstant())).toFile(GlobalConfig.APPLICATION_PATH_OUTPUT));
        });

    }
}
