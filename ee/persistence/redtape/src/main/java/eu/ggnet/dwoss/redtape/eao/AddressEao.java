package eu.ggnet.dwoss.redtape.eao;

import javax.persistence.*;

import eu.ggnet.dwoss.redtape.entity.Address;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author pascal.perau
 */
public class AddressEao extends AbstractEao<Address> {

    private EntityManager em;

    public AddressEao(EntityManager em) {
        super(Address.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Find a Address via correct formatted String.
     * The convention is: street + \n + zipCode + \n + city
     * <p>
     * @param address
     * @return a Address via correct formatted String.
     */
    public Address findByDescription(String address) {
        Query query = em.createNamedQuery("byFormatedString");
        query.setParameter(1, address);
        try {
            return (Address)query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

}
