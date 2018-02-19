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
package tryout;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.redtapext.ui.cao.document.annulation.CreditMemoView;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.Dl;

import static org.mockito.Mockito.mock;

/**
 *
 * @author oliver.guenther
 */
public class AnnulationViewTryout {

    public static void main(String[] args) {
        StockAgent stockStub = mock(StockAgent.class);

        Dl.remote().add(StockAgent.class, stockStub);

        Position p1 = Position.builder().amount(1).name("P1").price(12.).tax(1.19).build();
        Position p2 = Position.builder().amount(1).name("P2").price(20.).tax(1.19).build();
        Position p3 = Position.builder().amount(1).name("P3").price(13.24).tax(1.19).build();
        Position p4 = Position.builder().amount(1).name("P4").price(400.).tax(1.19).build();
        Position p5 = Position.builder().amount(1).name("P5").price(1234.).tax(1.19).build();

        List<AfterInvoicePosition> positions = new ArrayList<>();
        positions.add(new AfterInvoicePosition(p1));
        positions.add(new AfterInvoicePosition(p2));
        positions.add(new AfterInvoicePosition(p3));
        positions.add(new AfterInvoicePosition(p4));
        positions.add(new AfterInvoicePosition(p5));

        CreditMemoView view = new CreditMemoView(positions);
        OkCancelDialog<CreditMemoView> dialog = new OkCancelDialog<>("Test", view);
        dialog.setVisible(true);
        System.out.println(view.getPositions());
        System.exit(0);
    }

}
