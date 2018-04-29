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
package eu.ggnet.dwoss.misc.ui.cap.support;

import java.time.*;
import java.util.*;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.dwoss.common.ee.Step;

/**
 * Shows a selector pane for the Revenue Report.
 * <p>
 * @author oliver.guenther
 */
public class InputReportSelectorPane extends GridPane {

    private final ObjectProperty<Step> step = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDate> start;

    private final ObjectProperty<LocalDate> end;

    public InputReportSelectorPane() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        ChoiceBox<Step> stepChoice = new ChoiceBox<>();
        stepChoice.getItems().addAll(Step.values());
        step.bind(stepChoice.getSelectionModel().selectedItemProperty());
        stepChoice.getSelectionModel().select(Step.DAY);

        addRow(0, new Label("Step:"), stepChoice);

        DatePicker startPicker = new DatePicker(LocalDate.of(2014, 01, 01));
        start = startPicker.valueProperty();

        DatePicker endPicker = new DatePicker(LocalDate.of(2014, 12, 31));
        end = endPicker.valueProperty();

        addRow(1, new Label("Start:"), startPicker);
        addRow(2, new Label("End:"), endPicker);

    }

    public Step getStep() {
        return step.get();
    }

    public Date getStart() {
        return Date.from(start.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getEnd() {
        return Date.from(end.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "RevenueReportSelectorPane{" + "step=" + getStep() + ",start=" + start.get() + ",end=" + end.get() + '}';
    }

}
