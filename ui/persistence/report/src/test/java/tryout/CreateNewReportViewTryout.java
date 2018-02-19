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

import java.util.EnumSet;

import javax.swing.JLabel;

import eu.ggnet.dwoss.mandator.Mandators;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.report.ui.cap.support.CreateNewReportView;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.saft.*;
import eu.ggnet.saft.core.swing.OkCancelWrap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author oliver.guenther
 */
public class CreateNewReportViewTryout {

    public static void main(String[] args) {
        Mandators mandators = mock(Mandators.class);
        when(mandators.loadContractors()).thenReturn(new Contractors(EnumSet.allOf(TradeName.class), EnumSet.allOf(TradeName.class)));

        Dl.remote().add(Mandators.class, mandators);

        UiCore.startSwing(() -> new JLabel("Application"));

        Ui.build().swing().eval(() -> OkCancelWrap.vetoResult(new CreateNewReportView())).opt().ifPresent(v -> System.out.println(v.getPayload().getParameter()));

    }

}
