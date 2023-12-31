/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao.stateaction;

import java.util.*;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.core.common.values.PaymentSettlement;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.Bind;
import eu.ggnet.saft.core.ui.ResultProducer;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.core.common.values.PaymentSettlement.*;
import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

/**
 *
 * @author oliver.guenther
 */
@Dependent
public class SettlementPane extends VBox implements ResultProducer<PaymentSettlement> {

    @Bind(SHOWING)
    private BooleanProperty showing = new SimpleBooleanProperty();

    private boolean ok = false;

    private ToggleGroup tg;

    public SettlementPane() {
        setPadding(new Insets(5));
        setSpacing(5);
        tg = new ToggleGroup();
        List<RadioButton> buttons = createButtons(PAYPAL,E_CASH,REMITTANCE,EBAY,CREDIT_CARD);
        tg.getToggles().addAll(buttons);

        Button okButton = new Button("Ok");
        okButton.setOnAction(t -> {
            if ( tg.getSelectedToggle() == null ) {
                UiCore.global().build(this).alert("Keine Zahlungsmethode ausgewählt");
            } else {
                this.ok = true;
                showing.set(false);
            }
        });
        okButton.setPrefWidth(120);

        getChildren().addAll(buttons);
        getChildren().add(okButton);
    }

    private List<RadioButton> createButtons(PaymentSettlement... settlements) {
        if ( settlements == null || settlements.length == 0 ) return Collections.emptyList();
        List<RadioButton> result = new ArrayList<>();
        for (PaymentSettlement settlement : settlements) {
            RadioButton b = new RadioButton(settlement.description());
            b.setUserData(settlement);
            result.add(b);
        }
        return result;
    }

    @Override
    public PaymentSettlement getResult() {
        if ( !ok ) return null;
        if (tg.getSelectedToggle() == null) return null;
        return (PaymentSettlement)tg.getSelectedToggle().getUserData();
    }

}
