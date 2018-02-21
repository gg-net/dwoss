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
package tryout;

import javax.swing.JLabel;

import javafx.scene.control.ChoiceDialog;

import eu.ggnet.dwoss.misc.repayment.ResolveRepaymentController;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

import static eu.ggnet.dwoss.rights.api.AtomicRight.RESOLVE_REPAYMENT;
import static eu.ggnet.dwoss.rules.TradeName.ACER;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepayment {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new JLabel("Main Applikation"));
        // selector();
        run();
    }

    public static void selector() {
        Ui.exec(() -> {
            Ui.build().dialog().eval(() -> {
                ChoiceDialog<TradeName> dialog = new ChoiceDialog<>(ACER, TradeName.values());
                dialog.setTitle("Gutschriften");
                dialog.setHeaderText(RESOLVE_REPAYMENT.toName());
                dialog.setContentText("Lieferant auswählen:");
                return dialog;
            }).opt().ifPresent(System.out::println);
        });
    }

    public static void run() {
        Ui.exec(() -> {
            Ui.build().fxml().show(ResolveRepaymentController.class);
        });
    }
}
