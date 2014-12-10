/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.repayment;

import java.util.*;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.api.ui.Title;

/**
 *
 * @author oliver.guenther
 * <p>
 */
@Title("Lieferant für Gutschriftenabgleich wählen")
public class ContractorSelectorPane extends BorderPane implements Consumer<List<TradeName>> {

    private final ComboBox<TradeName> contractorBox;

    public ContractorSelectorPane() {
        setPadding(new Insets(10));
        contractorBox = new ComboBox<>();
        setCenter(contractorBox);
    }

    public TradeName selectedContactor() {
        return contractorBox.getValue();
    }

    @Override
    public void accept(List<TradeName> tradeNames) {
        contractorBox.setItems(FXCollections.observableList(tradeNames));
        contractorBox.getSelectionModel().selectFirst();
    }

}
