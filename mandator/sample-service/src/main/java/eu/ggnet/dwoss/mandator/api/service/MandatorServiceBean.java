package eu.ggnet.dwoss.mandator.api.service;

import eu.ggnet.dwoss.rules.TradeName;

import javax.ejb.Stateless;


/**
 * Sample Service Implementation.
 */
@Stateless
public class MandatorServiceBean implements MandatorService {

    @Override
    public boolean isAllowedRefurbishId(TradeName contractor, String refurbishId) {
        return true;

    }

    @Override
    public int getLocationStockId(ClientLocation location) {
        return 0;
    }

}
