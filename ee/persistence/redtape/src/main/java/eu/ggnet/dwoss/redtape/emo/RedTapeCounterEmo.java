package eu.ggnet.dwoss.redtape.emo;

import javax.persistence.EntityManager;

import eu.ggnet.dwoss.redtape.eao.RedTapeCounterEao;
import eu.ggnet.dwoss.redtape.entity.RedTapeCounter;

import eu.ggnet.dwoss.rules.DocumentType;

/**
 * Emo for RedTapeCounter.
 *
 * @author oliver.guenther
 */
public class RedTapeCounterEmo {

    private EntityManager em;

    public RedTapeCounterEmo(EntityManager em) {
        this.em = em;
    }

    /**
     * Requests a RedTapeCounter and increments the result.
     *
     * @param type   the type of counter.
     * @param prefix the prefix of the counter (normally YY)
     * @return a RedTapeCounter with incremented Result
     */
    public RedTapeCounter requestNext(DocumentType type, String prefix) {
        RedTapeCounterEao eao = new RedTapeCounterEao(em);
        RedTapeCounter singleResult = eao.findByCompositeKey(type, prefix);
        if ( singleResult == null ) {
            singleResult = new RedTapeCounter(type, prefix);
            em.persist(singleResult);
        }
        singleResult.setValue(singleResult.getValue() + 1);
        return singleResult;
    }
}
