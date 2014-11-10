package eu.ggnet.dwoss.rights;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rights.assist.Rights;
import eu.ggnet.dwoss.rights.eao.OperatorEao;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;

/**
 * This is the {@link RemoteAgent} for Rights.
 * <p>
 * @author Bastian Venz
 */
@Stateless
public class RightsAgentBean extends AbstractAgentBean implements RightsAgent {

    @Inject
    @Rights
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Persona store(Persona p) {
        if ( p.getId() == 0 ) {
            em.persist(p);
            return p;
        }
        return em.merge(p);
    }

    @Override
    public Operator store(Operator o) {
        if ( o.getId() == 0 ) {
            em.persist(o);
            return o;
        }
        return em.merge(o);
    }

    /**
     * Search a Operator by the Username and fetch it eager.
     * <p>
     * @param username
     * @return
     */
    @Override
    public Operator findOperatorByUsername(String username) {
        Operator singleResult = new OperatorEao(em).findByUsername(username);
        singleResult.fetchEager();
        return singleResult;
    }

}
