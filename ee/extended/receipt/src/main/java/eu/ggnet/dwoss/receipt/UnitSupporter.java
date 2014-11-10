package eu.ggnet.dwoss.receipt;

import javax.ejb.Remote;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface UnitSupporter {

    /**
     * Returns true if supplied refurbishId is available.
     *
     * @param refurbishId the refubishedId
     * @return true if available.
     */
    boolean isRefurbishIdAvailable(String refurbishId);

    /**
     * Returns true if supplied serial is available.
     * <p/>
     * @param serial the serial
     * @return true if available.
     */
    boolean isSerialAvailable(String serial);

    /**
     * Returns a refurbishId if a unit with the serial was in stock, otherwise null.
     * <p/>
     * @param serial the serial
     * @return a refurbishId if a unit with the serial was in stock, otherwise null.
     */
    String findRefurbishIdBySerial(String serial);
}
