/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ui.cap;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import javafx.scene.control.TextInputDialog;

import eu.ggnet.dwoss.core.widget.AccessableAction;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.ui.ScrapController;
import eu.ggnet.dwoss.stock.ui.ScrapResultController;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.UiUtil;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.DELETE_UNIT;

/**
 * Scraps multiple units.
 *
 * @author oliver.guenther
 */
@Dependent
public class DeleteUnitsAction extends AccessableAction {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Guardian guardian;

    public DeleteUnitsAction() {
        super(DELETE_UNIT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        saft.build().dialog()
                .eval(() -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Löschung");
                    dialog.setHeaderText("(Mehrere) SopoNummer(n) löschen.\nBei mehreren SopoNummern, Diese durch Freizeichen oder Kommas trennen.");
                    dialog.setContentText("SopoNr(n)");
                    return dialog;
                }).cf()
                .thenApply(rids -> {
                    Scanner s = new Scanner(rids);
                    s.useDelimiter("(\\ |\\,)");
                    return s.tokens().filter(t -> !(t == null || t.isBlank())).collect(Collectors.toList());
                })
                .thenApply(rids -> remote.lookup(StockApi.class).findByRefurbishIds(rids))
                .thenCompose((Map<String, SimpleStockUnit> m) -> saft.build().title("Löschung").fxml().eval(() -> m, ScrapController.class).cf())
                .thenApply((s) -> UiUtil.exceptionRun(() -> remote.lookup(StockApi.class).delete(s.ids(), s.comment(), guardian.getUsername())))
                .thenAccept(r -> saft.build().title("Ergebniss der Löschung").fxml().show(() -> r, ScrapResultController.class))
                .handle(saft.handler());
    }
}
