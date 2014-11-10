package eu.ggnet.dwoss.mandator.api.service;

import eu.ggnet.dwoss.redtape.api.RedTapeHookService;

import java.util.List;

import javax.ejb.Stateless;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;

/**
 * Sample implementation of the position service.
 * This service class will allow elaboration of positions based on a variety of parameters.
 * <p>
 * @author pascal.perau
 */
@Stateless
public class SampleHookServiceBean implements RedTapeHookService {

    @Override
    public Result<List<eu.ggnet.dwoss.redtape.entity.Position>> elaborateUnitPosition(eu.ggnet.dwoss.redtape.entity.Position p, long documentId) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Result<List<eu.ggnet.dwoss.redtape.entity.Position>> addWarrantyForUnitPosition(eu.ggnet.dwoss.redtape.entity.Position p, long documentId) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
