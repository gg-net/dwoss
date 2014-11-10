package eu.ggnet.dwoss.redtape.api;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.redtape.entity.Position;

/**
 *
 * @author pascal.perau
 */
@Remote
@Local
public interface PositionService {
    
    List<Position> servicePositionTemplates();
    
}
