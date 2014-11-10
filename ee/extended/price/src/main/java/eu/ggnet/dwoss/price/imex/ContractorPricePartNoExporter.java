package eu.ggnet.dwoss.price.imex;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 * The Contractor Exporter.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface ContractorPricePartNoExporter {

    /**
     * Exports all Products which have no Contractor Reference Price and AddiditionalPartNo of the supplied contractor.
     *
     * @param contractor the contractor as filter
     * @return a file jacket wrapping an XLS Document containing PartNo and Name of the incomplete products.
     */
    FileJacket toContractorXls(TradeName contractor);

    /**
     * Exports all Products which have no Costprice of the manufacturer.
     *
     * @param contractor a contractor which is also a manufacturer.
     * @return a file jacket wrapping an XLS Document containing PartNo and Name of the incomplete products.
     */
    FileJacket toManufacturerXls(TradeName contractor);
}
