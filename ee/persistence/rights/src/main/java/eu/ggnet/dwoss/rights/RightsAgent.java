package eu.ggnet.dwoss.rights;

import javax.ejb.*;

import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

/**
 *
 * @author Bastian Venz
 */
@Remote
@Local
public interface RightsAgent extends RemoteAgent {

    /**
     * Store, either persiting a new or merging an existing instance.
     * <p>
     * @param object
     * @return the stored instance
     */
    public Persona store(Persona object);

    /**
     * Store, either persiting a new or merging an existing instance.
     * <p>
     * @param object
     * @return the stored instance
     */
    public Operator store(Operator object);

    /**
     * Finds a Operator with the given username.
     * <p>
     * @param username is the username which the Operator should have.
     * @return a Operator with the given username.
     */
    public Operator findOperatorByUsername(String username);

}
