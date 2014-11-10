package eu.ggnet.dwoss.misc.op;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface PersistenceValidator {

    /**
     * This Method Validate all Databases.
     * It's validate:
     * - RedTape
     * - UniqueUnit
     * - Sopo
     * - Stock
     * <p/>
     * @return a Filejacket where a xls from the JExcel api is, that contains all Errors.
     */
    FileJacket validateDatabase();
}
