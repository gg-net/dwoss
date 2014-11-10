package eu.ggnet.dwoss.rights.eao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rights.assist.Rights;
import eu.ggnet.dwoss.rights.entity.Operator;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.mysema.query.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.entity.QOperator.operator;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class OperatorEao extends AbstractEao<Operator> {

    @Inject
    @Rights
    private EntityManager em;

    public OperatorEao() {
        super(Operator.class);
    }

    public OperatorEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Operator findByUsername(String username) {
        return new JPAQuery(em).from(operator).where(operator.username.eq(username)).singleResult(operator);
    }

}
