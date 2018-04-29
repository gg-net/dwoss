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
package eu.ggnet.dwoss.receipt.ui.tryout;

import javax.swing.UIManager;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.ProductProcessorStub;
import eu.ggnet.dwoss.receipt.ui.product.DesktopView;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;

/**
 *
 * @author oliver.guenther
 */
public class DesktopViewTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ProductProcessorStub receipt = new ProductProcessorStub();
        DesktopView view = new DesktopView(receipt.getSpecAgentStub(), ProductGroup.DESKTOP);
        view.setSpec(receipt.desktop);
        OkCancelDialog<DesktopView> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        System.out.println(create.getSubContainer().getSpec());
        System.out.println(view.getSpec().getHdds());
        System.out.println(view.getSpec().getOdds());
        System.out.println(SpecFormater.toSingleLine(view.getSpec()));
        System.exit(0);
    }

}
