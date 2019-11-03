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

import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.ProductProcessorStub;
import eu.ggnet.dwoss.receipt.ui.product.EditCpuPanel;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

/**
 *
 * @author oliver.guenther
 */
public class EditCpuPanelTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Cpu cpu = new Cpu(Cpu.Series.CORE_I7, "930", Cpu.Type.DESKTOP, 3.00, 4);

        EditCpuPanel view = new EditCpuPanel(new ProductProcessorStub(), new ArrayList<Cpu>());
        view.setCpu(cpu);
        OkCancelDialog<EditCpuPanel> create = new OkCancelDialog<>("Spezifikationen", view);
        create.setVisible(true);
        if ( create.getCloseType() == CloseType.OK ) {
            System.out.println(view.getCpu());
        }
        System.exit(0);
    }

}
