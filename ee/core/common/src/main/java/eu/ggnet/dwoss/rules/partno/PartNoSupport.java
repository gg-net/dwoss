package eu.ggnet.dwoss.rules.partno;

import java.io.Serializable;

/**
 * A support interface for PartNos.
 * <p>
 * @author oliver.guenther
 */
public interface PartNoSupport extends Serializable {

    /**
     * Returns true if a partNo is valid.
     * <p>
     * @param partNo the part no
     * @return true if a partNo is valid.
     */
    boolean isValid(String partNo);

    /**
     * Returns a string representation of the violation of the partNo or null if the partNo is valid.
     * <p>
     * @param partNo the partNo
     * @return a string representation of the violation of the partNo or null if the partNo is valid.
     */
    String violationMessages(String partNo);

    /**
     * Tries to normalise a partNo.
     * This might be punkations or else.
     * <p>
     * @param partNo the partNo to normalize
     * @return even if nothing matches, at least the parameter.
     */
    String normalize(String partNo);

}
