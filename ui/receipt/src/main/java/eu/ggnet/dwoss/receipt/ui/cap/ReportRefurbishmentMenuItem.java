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
package eu.ggnet.dwoss.receipt.ui.cap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.reporting.RefurbishmentReporter;
import eu.ggnet.dwoss.receipt.ui.ReportRefurbishmentController;
import eu.ggnet.saft.core.Saft;

/**
 *
 * @author mirko.schulze
 */
public class ReportRefurbishmentMenuItem extends MenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    public ReportRefurbishmentMenuItem() {
        super("Refurbishmentreport");
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> saft.build()
                .fxml()
                .eval(ReportRefurbishmentController.class)
                .cf()
                .thenAccept(result -> FileUtil.osOpen(Progressor.global().run("Refurbishmentreporter",
                () -> remote.lookup(RefurbishmentReporter.class).toXls(result.getTradeName(), result.getStart(), result.getEnd()).toTemporaryFile())))
                .handle(saft.handler())
        );
    }

}
