/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.redtape.position;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.rules.PositionType.SERVICE;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.rules.TradeName.APPLE;

/**
 *
 * @author oliver.guenther
 */
//TODO: might be removed due to the PositionService interface
@Deprecated
public class ServicePositionTemplates {

    public static final List<Position> GGNET = new ArrayList<>();

    public static final List<Position> ELUS = new ArrayList<>();

    public static Position[] by(Mandator mandator) {
        if ( mandator.getReceiptMode() == ACER ) return GGNET.toArray(new Position[0]);
        if ( mandator.getReceiptMode() == APPLE ) return ELUS.toArray(new Position[0]);
        throw new IllegalArgumentException("TradeName " + mandator.getReceiptMode() + " not supported");
    }

    private static void service(List<Position> toAdd, String name, String description, double afterTaxPrice, int bookingAccount) {
        Position build = Position.builder()
                .name(name).description(description).bookingAccount(bookingAccount)
                .afterTaxPrice(afterTaxPrice).tax(GlobalConfig.TAX).type(SERVICE).price(afterTaxPrice / (GlobalConfig.TAX + 1)).build();
        toAdd.add(build);
    }

    static {
        service(GGNET, "Dienstleistung", "Pauschale", 49., 8403);
        service(GGNET, "Software", "Microsoft Office 2013 Home & Student\nProduct Key - kein Datenträger", 119., 8403);
        service(GGNET, "16GB USB Memory Stick", "Transcend JetFlash 700", 14.90, 8415);
        service(GGNET, "Mouse", "Logitech M100", 14.90, 8415);
        service(GGNET, "Wireless Mouse", "Logitech XXXX", 19.90, 8415);
        service(GGNET, "Externes USB Laufwerk", "DVD/RW", 59., 8415);
        service(GGNET, "Externe HDD", "500GB, 2.5\", USB 3.0", 89., 8415);
        service(GGNET, "Netzteil für Acer Notebooks", "19V / 3.24A", 27.90, 8415);
        service(GGNET, "Notebooktasche", "15.6\"", 24.90, 8415);
        service(GGNET, "Zubehör", "", 0., 8415);

        service(ELUS, "Gebühren PayPal", "Gebühren PayPal", 1, 8403);
        service(ELUS, "Gebühren Ebay", "Gebühren Ebay", 1, 8403);
        service(ELUS, "Dienstleistung", "Pauschale", 49., 8403);

    }

}
