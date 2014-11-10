
package eu.ggnet.dwoss.uniqueunit.op;

import javax.ejb.Remote;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

/**
 *This is a {@link Remote} Interface to add a {@link UnitHistory} to a {@link UniqueUnit}. 
 * @author bastian.venz
 */
@Remote
public interface AddUnitHistory {
    void addCommentHistory(String refurbishId, String comment, String arranger);
}
