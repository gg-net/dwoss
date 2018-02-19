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
package tryout.old;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ui.old.AdditionalCustomerIdsView;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class AdditionalcustomerIdsViewTryout {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new JLabel("Main"));

        Map<ExternalSystem, String> in = new HashMap<>();
        in.put(ExternalSystem.SAGE, "123412");

        Ui.build().dialog().eval(() -> in, () -> new AdditionalCustomerIdsView()).opt().ifPresent(System.out::println);

    }

}
