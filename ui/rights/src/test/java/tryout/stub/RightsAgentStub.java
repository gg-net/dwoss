package tryout.stub;

import java.util.*;

import javax.persistence.LockModeType;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

/**
 *
 * @author Bastian Venz
 */
public class RightsAgentStub implements RightsAgent {

    Map<String, Persona> personas = new HashMap<>();

    Map<String, Operator> operators = new HashMap<>();

    {
        for (int i = 0; i < 3; i++) {
            Persona persona = new Persona();
            persona.setName("Persona " + i);
            persona.addAll(getRandomRights());
            personas.put(persona.getName(), persona);
        }
        for (int j = 0; j < 3; j++) {
            Operator operator = new Operator();
            for (AtomicRight atomicRight : getRandomRights()) {
                operator.add(atomicRight);
            }
            operator.setUsername("User " + j);
            int till = (int)(Math.random() * 3 - 1);
            for (Persona persona : new ArrayList<>(personas.values()).subList(0, till)) {
                operator.add(persona);
            }
            operator.setSalt(RandomStringUtils.randomAlphanumeric(6).getBytes());
            operator.setPassword(RandomStringUtils.randomAlphanumeric(5).getBytes());
            operator.setQuickLoginKey((int)(Math.random() * 999));
            operators.put(operator.getUsername(), operator);
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
    public Persona store(Persona persona) {
        if ( personas.containsKey(persona.getName()) ) {
            System.out.println("Persona Removed=" + persona);
            personas.remove(persona.getName());
        }
        System.out.println("Persona Stored=" + persona);
        personas.put(persona.getName(), persona);

        return persona;
    }

    @Override
    public Operator store(Operator operator) {
        if ( operators.containsKey(operator.getUsername()) ) {
            operators.remove(operator.getUsername());
            System.out.println("Operator Removed=" + operator);
        }
        System.out.println("Operator Stored=" + operator);
        operators.put(operator.getUsername(), operator);
        return operator;
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
            return (List<T>)new ArrayList<>(operators.values());
        } else {
            return (List<T>)new ArrayList<>(personas.values());
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
            for (Operator operator : operators.values()) {
                if ( operator.getId() == ((Long)id) )
                    return (T)operator;
            }
        } else {
            for (Persona persona : personas.values()) {
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
