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
package eu.ggnet.dwoss.misc.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.dwoss.common.ee.Css;
import eu.ggnet.dwoss.common.ui.HtmlPane;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author pascal.perau
 */
public class ShowMandatorAction extends AbstractAction {

    public ShowMandatorAction() {
        super("Aktiver Mandant");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().fx().show(() -> Css.toHtml5WithStyle(Dl.local().lookup(CachedMandators.class).loadMandator().toHtml()), () -> new HtmlPane());
        });
    }
}
