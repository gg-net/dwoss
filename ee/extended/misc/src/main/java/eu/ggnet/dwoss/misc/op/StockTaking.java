package eu.ggnet.dwoss.misc.op;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;

import eu.ggnet.dwoss.util.FileJacket;

import lombok.Value;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface StockTaking {

    @Value
    public static class UnitLine implements Serializable {

        private final String refurbisId;

        private final String partNo;

        private final String name;

        private final String shipment;

        private final Date inputDate;

        private final String balancingId;

        public UnitLine(UniqueUnit uu, String balancingId) {
            refurbisId = uu.getRefurbishId();
            partNo = (uu.getProduct() == null ? "" : uu.getProduct().getPartNo());
            name = ProductFormater.toName(uu.getProduct());
            shipment = uu.getShipmentLabel();
            inputDate = uu.getInputDate();
            this.balancingId = balancingId;
        }
    }

    /**
     * Takes the supplied list of refurbishIds, validates their existence in the supplied Stock or all if none supplied.
     *
     * @param inFile  a XLS File containing the refurbishIds in the first sheet, first column.
     * @param stockId the stock, may be null
     * @return a FileJacket with the Result as XLS Report.
     */
    FileJacket fullfillDetails(FileJacket inFile, Integer stockId);

    /**
     * Returns a List of Unit information identified by partNos and filtered by InputDate.
     * <p/>
     * @param partNos the partNos
     * @param start   the start of inputDate
     * @param end     the end of inputDate
     * @return a List of Unit information identified by partNos and filtered by InputDate.
     */
    List<UnitLine> units(Collection<String> partNos, Date start, Date end);
}
