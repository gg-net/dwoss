/*
 * Copyright (C) 2014 GG-Net GmbH
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

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.Dl;

import javax.swing.JLabel;

import eu.ggnet.dwoss.redtapext.ui.cao.document.position.ServiceViewCask;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.dwoss.common.ui.saftwrap.OkCancelWrap;

import static eu.ggnet.dwoss.core.common.values.TaxType.GENERAL_SALES_TAX_DE_SINCE_2007;

/**
 *
 * @author oliver.guenther
 */
public class ServiceViewCaskTryout {

    public static void main(String[] args) {
        
        Dl.local().add(RemoteLookup.class,new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });

            UiCore.startSwing(() -> new JLabel("Main Applikation"));

// () -> Position.builder().type(PositionType.SERVICE).price(30.).build()
            Ui.build().swing().eval(() -> OkCancelWrap.consumerVetoResult(new ServiceViewCask(GENERAL_SALES_TAX_DE_SINCE_2007)))
                    .opt().ifPresent(System.out::println);
        
    }

}
