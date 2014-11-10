package eu.ggnet.dwoss.misc.op.movement;

import javax.ejb.Remote;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.entity.Stock;

import lombok.*;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface MovementListingProducer {

    @RequiredArgsConstructor
    @Getter
    public static enum ListType {

        SHIPMENT("Versandliste", Document.Directive.PREPARE_SHIPPING),
        PICK_UP("Abholliste", Document.Directive.HAND_OVER_GOODS);

        private final String name;

        private final Document.Directive directive;
    }

    JasperPrint generateList(ListType listType, Stock stockId);
}
