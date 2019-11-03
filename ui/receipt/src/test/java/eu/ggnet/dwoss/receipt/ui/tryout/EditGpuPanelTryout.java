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

import java.util.ArrayList;

import javax.swing.UIManager;

import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.ProductProcessorStub;
import eu.ggnet.dwoss.receipt.ui.product.EditGpuPanel;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu.Series;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu.Type;

/**
 *
 * @author oliver.guenther
 */
public class EditGpuPanelTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        Gpu gpu = new Gpu(Type.MOBILE, Series.GEFORCE_500, "580");

        EditGpuPanel view = new EditGpuPanel(new ProductProcessorStub(), new ArrayList<Gpu>());
        view.setGpu(gpu);
        OkCancelDialog<EditGpuPanel> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        System.out.println(view.getGpu());
        System.exit(0);

    }
}
