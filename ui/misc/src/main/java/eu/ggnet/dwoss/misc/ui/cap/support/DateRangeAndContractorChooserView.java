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
package eu.ggnet.dwoss.misc.ui.cap.support;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.Title;

/**
 * A Date range and contractor chooser.
 *
 * @author oliver.guenther
 */
@Title("Datum und Lieferant")
public class DateRangeAndContractorChooserView extends BorderPane implements ResultProducer<DateRangeAndContractorChooserView.RangeAndContractor> {

    public static final class RangeAndContractor {

        public final LocalDate start;

        public final LocalDate end;

        public final TradeName contractor;

        public RangeAndContractor(LocalDate start, LocalDate end, TradeName contractor) {
            this.start = start;
            this.end = end;
            this.contractor = contractor;
        }

        public boolean isValid() {
            return violationMessages() == null;
        }

        public String violationMessages() {
            if ( start == null ) return "Start is not set";
            if ( end == null ) return "End is not set";
            if ( contractor == null ) return "Contractor not set";
            if ( end.isBefore(start) ) return end + " is before " + start;
            return null;
        }

        public Date startAsDate() {
            return Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }

        public Date endAsDate() {
            return Date.from(end.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }

    }

    private final DatePicker start;

    private final DatePicker end;

    private final static Border RED_BORDER = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private final static Border BLACK_BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    private RangeAndContractor result = null;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public DateRangeAndContractorChooserView() {
        setPadding(new Insets(10));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        start = new DatePicker();
        end = new DatePicker();

        ComboBox<TradeName> contractorBox = new ComboBox<>(FXCollections.observableArrayList(TradeName.values()));
        contractorBox.getSelectionModel().selectFirst();

        Button cancel = new Button("Abbrechen");
        cancel.setOnAction((e) -> Ui.closeWindowOf(this));
        Button ok = new Button("Ok");
        ok.setOnAction((e) -> {
            result = new RangeAndContractor(start.getValue(), end.getValue(), contractorBox.getSelectionModel().getSelectedItem());
            if ( result.isValid() ) Ui.closeWindowOf(this);
            else Ui.build(this).alert(result.violationMessages());
        });

        FlowPane flowPane = new FlowPane(10, 10, ok, cancel);
        flowPane.setAlignment(Pos.BOTTOM_RIGHT);
        setTop(new VBox(10, new HBox(10, new Label("Start:"), start), new HBox(10, new Label("Ende:"), end), new HBox(10, new Label("Lieferant:"), contractorBox)));
        setBottom(flowPane);
        setPrefSize(350, 170);

        // -- bind
        ok.disableProperty().bind(start.valueProperty().isNull().or(end.valueProperty().isNull()));

    }

    @Override
    public RangeAndContractor getResult() {
        return result;
    }

}
