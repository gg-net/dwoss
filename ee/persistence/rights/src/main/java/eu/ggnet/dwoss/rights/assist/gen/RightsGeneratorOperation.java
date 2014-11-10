package eu.ggnet.dwoss.rights.assist.gen;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.assist.Rights;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

/**
 *
 * @author Bastian Venz
 */
@Stateless
public class RightsGeneratorOperation {

    @Inject
    @Rights
    private EntityManager em;

    /**
     * Create the given amount of operators and personas and create a Addentional Operator with the Username "Adminuser" und clear text Password Test and All
     * Rights.
     * <p>
     * .* @param countOfOperator
     * <p>
     * @param countOfOperator
     * @param countOfPersona
     */
    public void make(int countOfOperator, int countOfPersona) {
        List<Persona> personas = new ArrayList<>();
        for (int i = 0; i < countOfPersona; i++) {
            Persona persona = new Persona();
            persona.setName("Persona " + i);
            persona.addAll(getRandomRights());
            em.persist(persona);
            personas.add(persona);
        }
        for (int j = 0; j < countOfOperator; j++) {
            Operator operator = new Operator();
            for (AtomicRight atomicRight : getRandomRights()) {
                operator.add(atomicRight);
            }
            operator.setUsername("User " + j);
            int till = (int)(Math.random() * countOfPersona - 1);
            for (Persona persona : personas.subList(0, till)) {
                operator.add(persona);
            }
            operator.setSalt(RandomStringUtils.randomAlphanumeric(6).getBytes());
            operator.setPassword(hashRandomString(RandomStringUtils.randomAlphanumeric(15), operator.getSalt()));
            operator.setQuickLoginKey((int)(Math.random() * 999));
            em.persist(operator);
        }
    }

    public Operator make(String username, Collection<AtomicRight> activeRights) {
        Operator o = new Operator();
        o.setUsername(username);
        for (AtomicRight right : activeRights) {
            o.add(right);
        }
        em.persist(o);
        return o;
    }

    private static byte[] hashRandomString(String password, byte[] salt) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().getBytes("UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }

    private static List<AtomicRight> getRandomRights() {
        List<AtomicRight> rights = Arrays.asList(AtomicRight.values());
        Collections.shuffle(rights);
        int till = (int)(Math.random() * rights.size() - 1) + 1;
        rights = rights.subList(0, till);
        return rights;
    }

}
