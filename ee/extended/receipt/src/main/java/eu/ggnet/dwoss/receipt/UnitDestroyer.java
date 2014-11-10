package eu.ggnet.dwoss.receipt;

import javax.ejb.Remote;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 * Remote Interface for UnitDestroyerOperation.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface UnitDestroyer {

    /**
     * Delete the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    void delete(UniqueUnit uniqueUnit, String reason, String arranger);

    /**
     * Scraps the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    void scrap(final UniqueUnit uniqueUnit, final String reason, final String arranger);

    /**
     * Validates if a unit identified by refurbishedId is scrapable.
     * Throws Exception if:
     * <ul>
     * <li>No UniqueUnit,SopoUnit or StockUnit exists.</li>
     * <li>StockUnit is inTransaction</li>
     * <li>SopoUnit is in Auftrag or Balanced.</li>
     * </ul>
     *
     * @param refurbishedId the refurbishedId
     * @return
     * @throws UserInfoException if not scrapable.
     */
    UniqueUnit verifyScarpOrDeleteAble(String refurbishedId) throws UserInfoException;
}
