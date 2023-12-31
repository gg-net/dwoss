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

import jakarta.inject.Inject;

import jakarta.annotation.PostConstruct;

import javafx.scene.control.MenuItem;

import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.reporting.RefurbishmentReporter;
import eu.ggnet.dwoss.receipt.ui.ReportRefurbishmentController;
import eu.ggnet.saft.core.Saft;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author mirko.schulze
 */
@Dependent
public class ReportRefurbishmentMenuItem extends MenuItem {

    @Inject
    private Saft saft;

    @Inject
    private RemoteDl remote;

    @Inject
    private Progressor progressor;

    public ReportRefurbishmentMenuItem() {
        super("Refurbishmentreport");
    }

    @PostConstruct
    private void init() {
        setOnAction(e -> saft.build().fxml().eval(ReportRefurbishmentController.class).cf()
                .thenApply(r -> progressor.run("Refurbishmentreporter", () -> remote.lookup(RefurbishmentReporter.class).toXls(r.getTradeName(), r.getStart(), r.getEnd()).toTemporaryFile()))
                .thenAccept(f -> FileUtil.osOpen(f))
                .handle(saft.handler())
        );
    }

}
