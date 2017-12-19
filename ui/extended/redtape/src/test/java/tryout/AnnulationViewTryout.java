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

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtapext.ui.cao.document.AfterInvoicePosition;
import eu.ggnet.dwoss.redtapext.ui.cao.document.annulation.CreditMemoView;
import eu.ggnet.dwoss.stock.StockAgent;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.Client;

import static org.mockito.Mockito.mock;

/**
 *
 * @author oliver.guenther
 */
public class AnnulationViewTryout {

    public static void main(String[] args) {
        StockAgent stockStub = mock(StockAgent.class);

        Client.addSampleStub(StockAgent.class, stockStub);

        Position p1 = new PositionBuilder().setName("P1").setPrice(12.).setTax(1.19).createPosition();
        Position p2 = new PositionBuilder().setName("P2").setPrice(20.).setTax(1.19).createPosition();
        Position p3 = new PositionBuilder().setName("P3").setPrice(13.24).setTax(1.19).createPosition();
        Position p4 = new PositionBuilder().setName("P4").setPrice(400.).setTax(1.19).createPosition();
        Position p5 = new PositionBuilder().setName("P5").setPrice(1234.).setTax(1.19).createPosition();

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
