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
package eu.ggnet.dwoss.redtape.document;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.ResultProducer;


/**
 *
 * @author oliver.guenther
 */
public class TaxChangePane extends FlowPane implements ResultProducer<Tax> {

    private Tax result = null;

    public TaxChangePane() {
        super(Orientation.VERTICAL);
        Button _19Percent = new Button(Tax._19PERCENT.getButtonText());
        _19Percent.setOnAction(e -> {
            result = Tax._19PERCENT;
            close();
        });
        Button reverseCharge = new Button(Tax.REVERSE_CHARGE.getButtonText());
        reverseCharge.setOnAction(e -> {
            result = Tax.REVERSE_CHARGE;
            close();
        });
        Button cancel = new Button("Abbrechen");
        cancel.setOnAction(e -> close());
    }

    @Override
    public Tax getResult() {
        return result;
    }

    private void close() {
        Ui.closeWindowOf(this);
    }

}
