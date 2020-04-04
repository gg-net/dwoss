/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.price.ui.cap;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import eu.ggnet.dwoss.price.ee.imex.ImportResult;
import eu.ggnet.saft.core.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@Title("Ergebnis des Imports")
public class ImportResultView extends BorderPane implements Consumer<ImportResult> {

    private final Label head = new Label();

    private final TabPane center = new TabPane();

    public ImportResultView() {
        head.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        setTop(head);
        center.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setCenter(center);
    }

    @Override
    public void accept(ImportResult ir) {
        if ( ir == null ) return;
        center.getTabs().clear();
        head.setText("Importierte Datensätze: " + ir.amountImported() + "" + (ir.amountImported() == 0 ? " Fehlerhafte Import Daten ?" : ""));
        TextArea ta = new TextArea();
        ta.setDisable(true);
        ta.setText(ir.summary());
        Tab summary = new Tab("Übersicht", ta);
        center.getTabs().add(summary);
        if ( !ir.infos().isEmpty() ) {
            ta = new TextArea();
            ta.setDisable(true);
            ta.setText(ir.infos().stream().collect(Collectors.joining("\n")));
            center.getTabs().add(new Tab("Infos", ta));
        }
        if ( !ir.errors().isEmpty() ) {
            ta = new TextArea();
            ta.setDisable(true);
            ta.setText(ir.errors().stream().collect(Collectors.joining("\n")));
            center.getTabs().add(new Tab("Fehler", ta));
        }
        center.getSelectionModel().select(summary);
    }

}
