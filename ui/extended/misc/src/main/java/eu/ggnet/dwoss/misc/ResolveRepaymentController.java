/*
 * Copyright (C) 2014 bastian.venz
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
package eu.ggnet.dwoss.misc;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepaymentController implements Initializable {

    @FXML
    private TableView<SimpleReportLine> reportLineTable;

    @FXML
    private TextField sopoField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void handleResolveButtonAction() {
    }

    @FXML
    public void handleCancelButtonAction() {
    }

    public static URL loadFxml() {
        return ResolveRepaymentController.class.getResource("ResolveRepayment.fxml");
    }
}
