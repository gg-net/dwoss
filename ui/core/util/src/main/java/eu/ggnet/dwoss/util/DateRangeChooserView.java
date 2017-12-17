/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.Once;
import eu.ggnet.saft.api.ui.ResultProducer;

import lombok.Value;

/**
 * A generic Date range chooser.
 *
 * @author oliver.guenther
 */
@Once(false)
public class DateRangeChooserView extends BorderPane implements ResultProducer<DateRangeChooserView.Range> {

    @Value
    public static final class Range {

        private final LocalDate start;

        private final LocalDate end;

        public boolean isValid() {
            return violationMessages() == null;
        }

        public String violationMessages() {
            if ( start == null ) return "Start is not set";
            if ( end == null ) return "End is not set";
            if ( end.isBefore(start) ) return end + " is before " + start;
            return null;
        }

        public Date getStartAsDate() {
            return Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }

        public Date getEndAsDate() {
            return Date.from(end.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }

    }

    private final DatePicker start;

    private final DatePicker end;

    private final static Border RED_BORDER = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private final static Border BLACK_BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private Range result = null;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DateRangeChooserView() {
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label startLabel = new Label("Start:");
        start = new DatePicker();
        Label endLabel = new Label("Ende:");
        end = new DatePicker();
        Button cancel = new Button("Abbrechen");
        cancel.setOnAction((e) -> Ui.closeWindowOf(this));
        Button ok = new Button("Ok");
        ok.setOnAction((e) -> {
            result = new Range(start.getValue(), end.getValue());
            if ( result.isValid() ) Ui.closeWindowOf(this);
            else eu.ggnet.saft.core.Alert.show(this, result.violationMessages());
        });

        FlowPane flowPane = new FlowPane(10, 10, ok, cancel);
        flowPane.setAlignment(Pos.BOTTOM_RIGHT);
        setTop(new VBox(10, new HBox(10, startLabel, start), new HBox(10, endLabel, end)));
        setBottom(flowPane);
        setPrefSize(330, 120);

        // -- bind
        ok.disableProperty().bind(start.valueProperty().isNull().or(end.valueProperty().isNull()));

    }

    @Override
    public Range getResult() {
        return result;
    }

}
