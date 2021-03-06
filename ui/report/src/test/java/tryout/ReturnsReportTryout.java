/*
 * Copyright (C) 2017 GG-Net GmbH
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ui.returns.ReturnsReportView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class ReturnsReportTryout {

    public static void main(String[] args) {
        ReportLineGenerator gen = new ReportLineGenerator();
        List<ReportLine> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(gen.makeReportLine());
        }

        UiCore.startSwing(() -> new JLabel("Main Applikation"));
        Ui.exec(() -> {
            Ui.build().swing().show(() -> list, () -> new ReturnsReportView());
        });
    }

}
