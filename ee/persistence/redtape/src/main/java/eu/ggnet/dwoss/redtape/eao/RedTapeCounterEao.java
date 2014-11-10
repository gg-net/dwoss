package eu.ggnet.dwoss.redtape.eao;

import javax.persistence.*;

import eu.ggnet.dwoss.redtape.entity.RedTapeCounter;

import eu.ggnet.dwoss.rules.DocumentType;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.mysema.query.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.redtape.entity.QRedTapeCounter.redTapeCounter;

/**
 * This is the EAO for the {@link RedTapeCounter}.
 * <p/>
 * @author bastian.venz
 */
public class RedTapeCounterEao extends AbstractEao<RedTapeCounter> {

    private EntityManager em;

    public RedTapeCounterEao(EntityManager em) {
        super(RedTapeCounter.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public RedTapeCounter findByCompositeKey(DocumentType type, String prefixString) {
        //      "SELECT i FROM RedTapeCounter AS i WHERE i.type = ?1 AND i.prefix = ?2"
        return new JPAQuery(em)
                .from(redTapeCounter)
                .where(redTapeCounter.type.eq(type).and(redTapeCounter.prefix.eq(prefixString)))
                .singleResult(redTapeCounter);
    }
}
