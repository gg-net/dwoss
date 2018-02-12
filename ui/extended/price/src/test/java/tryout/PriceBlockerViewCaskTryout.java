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


import eu.ggnet.dwoss.price.PriceBlockerViewCask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.swing.OkCancelWrap;


/**
 *
 * @author jens.papenhagen
 */
public class PriceBlockerViewCaskTryout {

    public static void main(String[] args) {

        
        PriceBlockerViewCask pbp = new PriceBlockerViewCask("TestUnit des Testens", "Hier wird getestets\n<b>BLARG</b>", 10d, 15d);

        Ui.exec(() -> {
            Ui.build().title("Test").swing().eval(() -> OkCancelWrap.result(pbp)).ifPresent(System.out::println);
        });
    }
}
