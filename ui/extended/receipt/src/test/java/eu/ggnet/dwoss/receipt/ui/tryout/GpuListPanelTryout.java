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

import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.ProductProcessorStub;
import eu.ggnet.dwoss.receipt.ui.product.GpuListController;
import eu.ggnet.dwoss.receipt.ui.product.GpuListPanel;

/**
 *
 * @author oliver.guenther
 */
public class GpuListPanelTryout {

    public static void main(String[] args) {
        GpuListController controller;
        ProductProcessorStub stub = new ProductProcessorStub();
        controller = new GpuListController(stub.getSpecAgentStub());
        OkCancelDialog<GpuListPanel> panel = new OkCancelDialog<>("blub", new GpuListPanel(controller));
        panel.setVisible(true);
        System.exit(0);

    }
}
