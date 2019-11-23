/*
 * Copyright (C) 2018 GG-Net GmbH
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

import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.report.ui.cap.support.CreateNewReportView;
import eu.ggnet.saft.core.*;

/**
 *
 * @author oliver.guenther
 */
public class CreateNewReportViewTryout {

    public static void main(String[] args) {
        Dl.remote().add(Mandators.class, new ManadatorsStub());
        Dl.local().add(CachedMandators.class, new ManadatorsStub());

        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Application"));
            Ui.build().swing().eval(() -> OkCancelWrap.vetoResult(new CreateNewReportView()))
                    .opt().ifPresent(v -> System.out.println(v.getPayload().getParameter()));
        });
    }

}
