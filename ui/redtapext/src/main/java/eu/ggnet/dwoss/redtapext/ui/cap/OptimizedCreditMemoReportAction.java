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
package eu.ggnet.dwoss.redtapext.ui.cap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.ReplyUtil;
import eu.ggnet.dwoss.common.ui.DateRangeChooserView;
import eu.ggnet.dwoss.redtapext.ee.reporting.CreditMemoReporter;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;

import static javax.swing.Action.NAME;

public class OptimizedCreditMemoReportAction extends AbstractAction {

    private static final Logger L = LoggerFactory.getLogger(OptimizedCreditMemoReportAction.class);

    public OptimizedCreditMemoReportAction() {
        putValue(NAME, "Stornoreport gekürzt");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            L.debug("Start generating OptimizedCreditMemoReport.");
            Ui.build().fx().eval(() -> new DateRangeChooserView())
                    .opt()
                    .map(r -> ReplyUtil.wrap(() -> Ui.osOpen(Dl.remote().lookup(CreditMemoReporter.class).toOptimizedXls(r.startAsDate(), r.endAsDate()).toTemporaryFile())))
                    .filter(Ui.failure()::handle);
            L.debug("Done generating OptimizedCreditMemoReport.");
        });
    }
}
