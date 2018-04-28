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
package eu.ggnet.dwoss.receipt.product;

import java.util.EnumSet;

import javax.swing.UIManager;

import eu.ggnet.dwoss.common.ui.CloseType;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.Color;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec.VideoPort;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;

/**
 *
 * @author lucas.huelsen
 */
public class BasicViewTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        BasicSpec basic = new BasicSpec();
        basic.setPartNo("ABX");
        basic.setColor(Color.RED);
        basic.setComment("Der große Kommentar");
        basic.setExtras(EnumSet.of(Extra.BLUETOOTH, Extra.CARD_READER));
        basic.setVideoPorts(EnumSet.of(VideoPort.HDMI, VideoPort.VGA));

        BasicView view = new BasicView();
        view.setSpec(basic);
        OkCancelDialog<BasicView> dialog = new OkCancelDialog<>("", view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            System.out.println(view.getSpec());
            System.out.println(view.getGtin());
        }
        Thread.sleep(1221);
        System.exit(0);
    }
}
