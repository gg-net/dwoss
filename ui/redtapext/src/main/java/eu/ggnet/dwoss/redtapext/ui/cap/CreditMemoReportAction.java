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

import eu.ggnet.dwoss.core.widget.*;
import eu.ggnet.dwoss.redtapext.ee.reporting.CreditMemoReporter;
import eu.ggnet.saft.core.Ui;

import jakarta.enterprise.context.Dependent;

/**
 * Action to create the CreditMemo Report.
 *
 * @author pascal.perau
 */
@Dependent
public class CreditMemoReportAction extends AbstractAction {

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public CreditMemoReportAction() {
        putValue(NAME, "Rekla/Storno-Report");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.exec(() -> {
            Ui.build().title("Rekla/Storno-Report Zeitraum").fx().eval(() -> new DateRangeChooserView()).opt()
                    .ifPresent(r -> FileUtil.osOpen(Progressor.global().run(() -> Dl.remote().lookup(CreditMemoReporter.class).toXls(r.startAsDate(), r.endAsDate()).toTemporaryFile())));
        });
    }
}
