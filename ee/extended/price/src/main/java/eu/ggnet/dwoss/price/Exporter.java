package eu.ggnet.dwoss.price;

import javax.ejb.Remote;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 * Remote Interface for the {@link ExporterOperation}.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface Exporter {

    /**
     * Export PriceManagement as Xls.
     * <p/>
     * @return PriceManagement as Xls.
     */
    FileJacket toXls();

    /**
     * Creates a price compare sheet, expects an xls file with the first column filed with partNos.
     *
     * @param inFile the infile
     * @return the price compare xls outfile.
     */
    FileJacket toXlsByXls(FileJacket inFile);

    /**
     * Loads exactly one Unit as PriceEngineResult.
     *
     * @param refurbishId the unitid
     * @return The PriceEngineResult or Null if Id not found
     * @throws UserInfoException if the unitId is not a Number
     */
    PriceEngineResult load(String refurbishId) throws UserInfoException;

    /**
     * Calculates a Price for on Unit.
     *
     * @param refurbishId the refurbishId
     * @return The PriceEngineResult or Null if Id not found
     */
    PriceEngineResult onePrice(String refurbishId);

}
