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

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.Dl;

import java.awt.event.ActionEvent;
import java.util.concurrent.*;

import org.openide.util.Lookup;

import eu.ggnet.dwoss.misc.ee.SalesChannelHandler;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.common.ui.AccessableAction;
import eu.ggnet.dwoss.misc.ui.saleschannel.*;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.experimental.auth.Guardian;

import static eu.ggnet.dwoss.rights.api.AtomicRight.OPEN_SALES_CHANNEL_MANAGER;

/**
 * Opens the SalesChannelManager with all available units and optional executes the changes.
 * <p>
 * @author oliver.guenther
 */
public class OpenSalesChannelManagerAction extends AccessableAction {

    public OpenSalesChannelManagerAction() {
        super(OPEN_SALES_CHANNEL_MANAGER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.build().swing().eval(
                () -> new SalesChannelManagerData(Dl.remote().lookup(SalesChannelHandler.class).findAvailableUnits(), Dl.remote().lookup(StockAgent.class).findAll(Stock.class)),
                () -> new SalesChannelManagerView())
                .cf()
                .thenApply(lines -> {
                    try {
                        return Dl.remote().lookup(SalesChannelHandler.class)
                                .update(lines, Dl.local().lookup(Guardian.class).getUsername(), "Erzeugt duch Verkaufskanalmanager");
                    } catch (UserInfoException ex) {
                        throw new CompletionException(ex);
                    }
                })
                .thenAccept(change -> {
                    Ui.build().alert().message((change ? "Verkaufskanaländerungen durchgeführt und Umfuhren vorbereitet" : "Keine Änderungen an Verkaufskanälen durchgeführt")).show();
                })
                .handle(Ui.handler());
    }
}
