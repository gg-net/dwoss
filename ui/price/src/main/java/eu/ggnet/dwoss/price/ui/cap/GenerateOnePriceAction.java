/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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

import java.awt.event.ActionEvent;

import javafx.scene.control.TextInputDialog;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.common.Css;
import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.core.widget.saft.Failure;
import eu.ggnet.dwoss.core.widget.saft.ReplyUtil;
import eu.ggnet.dwoss.price.ee.Exporter;
import eu.ggnet.dwoss.price.ee.engine.support.PriceEngineResultFormater;
import eu.ggnet.dwoss.core.widget.saft.Reply;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ONE_PRICE;
import static javafx.stage.Modality.WINDOW_MODAL;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class GenerateOnePriceAction extends AccessableAction {

    public GenerateOnePriceAction() {
        super(CREATE_ONE_PRICE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("Bitte SopoNr eingeben :").dialog().eval(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Bitte SopoNr eingeben :");
                return dialog;
            })
                    .opt()
                    .filter(s -> !StringUtils.isBlank(s))
                    .map(r -> ReplyUtil.wrap(() -> Dl.remote().lookup(Exporter.class).onePrice(r)))
                    .filter(Failure::handle)
                    .map(Reply::getPayload)
                    .ifPresent(p -> Ui.build().modality(WINDOW_MODAL).title("SopoNr")
                    .fx()
                    .show(() -> Css.toHtml5WithStyle(PriceEngineResultFormater.toSimpleHtml(p)), () -> new HtmlPane()));
        });
    }
}
