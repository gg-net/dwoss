package eu.ggnet.dwoss.redtape.position;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.rules.TradeName.APPLE;

/**
 *
 * @author oliver.guenther
 */
public class ServicePositionTemplates {

    public static final List<Position> GGNET = new ArrayList<>();

    public static final List<Position> ELUS = new ArrayList<>();

    public static Position[] by(Mandator mandator) {
        if ( mandator.getReceiptMode() == ACER ) return GGNET.toArray(new Position[0]);
        if ( mandator.getReceiptMode() == APPLE ) return ELUS.toArray(new Position[0]);
        throw new IllegalArgumentException("TradeName " + mandator.getReceiptMode() + " not supported");
    }

    private static void service(List<Position> toAdd, String name, String description, double afterTaxPrice, int bookingAccount) {
        Position servPosition = new Position();
        servPosition.setName(name);
        servPosition.setDescription(description);
        servPosition.setBookingAccount(bookingAccount);
        servPosition.setAfterTaxPrice(afterTaxPrice);
        servPosition.setTax(GlobalConfig.TAX);
        servPosition.setType(PositionType.SERVICE);
        servPosition.setPrice(afterTaxPrice / (GlobalConfig.TAX + 1));
        toAdd.add(servPosition);
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
