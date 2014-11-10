package eu.ggnet.dwoss.redtape.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.eao.AddressEao;
import eu.ggnet.dwoss.redtape.entity.Address;

/**
 *
 * @author pascal.perau
 */
public class AddressEmo {

    private EntityManager em;

    public AddressEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Get a Address by description search.
     *
     * @param searchAddress the address description to search for.
     * @return a Address by description search or a new persisted Address.
     */
    public Address request(String searchAddress) {
        AddressEao addressEao = new AddressEao(em);

        Address address = addressEao.findByDescription(searchAddress);

        //persist address if nothing is found
        if ( address == null ) {
            address = new Address(searchAddress);
            em.persist(address);
            return address;
        }
        return address;
    }
}
