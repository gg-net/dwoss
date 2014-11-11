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

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.report.eao.ReportLineEao.Step;
import eu.ggnet.dwoss.rules.TradeName;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Shows a selector pane for the Revenue Report.
 * <p>
 * @author oliver.guenther
 */
public class RevenueReportSelectorPane extends GridPane {

    private final ObjectProperty<Step> step = new SimpleObjectProperty<>();

    private final StringProperty contractor = new SimpleStringProperty();

    private final ObjectProperty<LocalDate> start;

    private final ObjectProperty<LocalDate> end;

    public RevenueReportSelectorPane() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        ChoiceBox<Step> stepChoice = new ChoiceBox<>();
        stepChoice.getItems().addAll(Step.values());
        step.bind(stepChoice.getSelectionModel().selectedItemProperty());
        stepChoice.getSelectionModel().select(Step.DAY);

        addRow(0, new Label("Step:"), stepChoice);

        ChoiceBox<String> contractorChoice = new ChoiceBox<>();
        contractorChoice.getItems().addAll(Client.lookup(MandatorSupporter.class).loadContractors()
                .all().stream().map(TradeName::name).collect(Collectors.toList()));
        contractorChoice.getItems().add("ALL");

        contractor.bind(contractorChoice.getSelectionModel().selectedItemProperty());
        contractorChoice.getSelectionModel().select("ALL");
        // Not yet implemented.
        contractorChoice.setDisable(true);

        addRow(1, new Label("Contractor:"), contractorChoice);

        DatePicker startPicker = new DatePicker(LocalDate.of(2014, 01, 01));
        start = startPicker.valueProperty();

        DatePicker endPicker = new DatePicker(LocalDate.of(2014, 12, 31));
        end = endPicker.valueProperty();

        addRow(2, new Label("Start:"), startPicker);
        addRow(3, new Label("End:"), endPicker);

    }

    public Step getStep() {
        return step.get();
    }

    public Set<TradeName> getContractors() {
        try {
            return EnumSet.of(TradeName.valueOf(contractor.get()));
        } catch (IllegalArgumentException e) {
            // Name does not match
            return Client.lookup(MandatorSupporter.class).loadContractors().all();
        }
    }

    public Date getStart() {
        return Date.from(start.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getEnd() {
        return Date.from(end.get().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "RevenueReportSelectorPane{" + "step=" + getStep() + ",contractors=" + getContractors() + ",start=" + start.get() + ",end=" + end.get() + '}';
    }

}
