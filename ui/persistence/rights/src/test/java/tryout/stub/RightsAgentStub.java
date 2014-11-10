package tryout.stub;

import java.util.*;

import javax.persistence.LockModeType;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.rights.RightsAgent;

import eu.ggnet.dwoss.rights.api.AtomicRight;

import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

/**
 *
 * @author Bastian Venz
 */
public class RightsAgentStub implements RightsAgent {

    List<Persona> personas = new ArrayList<>();

    List<Operator> operators = new ArrayList<>();

    {
        for (int i = 0; i < 3; i++) {
            Persona persona = new Persona();
            persona.setName("Persona " + i);
            persona.addAll(getRandomRights());
            personas.add(persona);
        }
        for (int j = 0; j < 3; j++) {
            Operator operator = new Operator();
            for (AtomicRight atomicRight : getRandomRights()) {
                operator.add(atomicRight);
            }
            operator.setUsername("User " + j);
            int till = (int)(Math.random() * 3 - 1);
            for (Persona persona : personas.subList(0, till)) {
                operator.add(persona);
            }
            operator.setSalt(RandomStringUtils.randomAlphanumeric(6).getBytes());
            operator.setPassword(RandomStringUtils.randomAlphanumeric(5).getBytes());
            operator.setQuickLoginKey((int)(Math.random() * 999));
            operators.add(operator);
        }
    }

    public Operator make(String username, Collection<AtomicRight> activeRights) {
        Operator o = new Operator();
        o.setUsername(username);
        for (AtomicRight right : activeRights) {
            o.add(right);
        }
        return o;
    }

    private static List<AtomicRight> getRandomRights() {
        List<AtomicRight> rights = Arrays.asList(AtomicRight.values());
        Collections.shuffle(rights);
        int till = (int)(Math.random() * rights.size() - 1) + 1;
        rights = rights.subList(0, till);
        return rights;
    }

    @Override
    public Persona store(Persona object) {
        System.out.println("Persona Stored=" + object);
        if ( !personas.contains(object) ) personas.add(object);
        return object;
    }

    @Override
    public Operator store(Operator object) {
        System.out.println("Operator Stored=" + object);
        if ( !operators.contains(object) ) operators.add(object);
        return object;
    }

    @Override
    public Operator findOperatorByUsername(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass == Operator.class ) {
            return operators.size();
        } else {
            return personas.size();
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass == Operator.class ) {
            return (List<T>)operators;
        } else {
            return (List<T>)personas;
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        return findAll(entityClass);
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        return findAll(entityClass);
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        if ( entityClass == Operator.class ) {
            for (Operator operator : operators) {
                if ( operator.getId() == ((Long)id) )
                    return (T)operator;
            }
        } else {
            for (Persona persona : personas) {
                if ( persona.getId() == ((Long)id) )
                    return (T)persona;
            }
        }
        return null;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        return findById(entityClass, id);
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        return findById(entityClass, id);
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        return findById(entityClass, id);
    }

}
