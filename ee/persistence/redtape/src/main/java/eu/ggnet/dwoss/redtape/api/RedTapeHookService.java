package eu.ggnet.dwoss.redtape.api;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.redtape.entity.Position;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;

/**
 * Remote interfaces to provide position template generators.
 * <p>
 * @author pascal.perau
 */
@Remote
@Local
public interface RedTapeHookService {

    Result<List<Position>> elaborateUnitPosition(Position p, long documentId) throws UserInfoException;

    Result<List<Position>> addWarrantyForUnitPosition(Position p, long documentId) throws UserInfoException;

}
