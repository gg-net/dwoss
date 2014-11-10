package eu.ggnet.dwoss.stock.eao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.entity.Shipment;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class StockEao extends AbstractEao<Shipment> {

    @Inject
    @Stocks
    private EntityManager em;

    /**
     * Default Constructor.
     *
     * @param em the Shipment Manager.
     */
    public StockEao(EntityManager em) {
        super(Shipment.class);
        this.em = em;
    }

    public StockEao() {
        super(Shipment.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
