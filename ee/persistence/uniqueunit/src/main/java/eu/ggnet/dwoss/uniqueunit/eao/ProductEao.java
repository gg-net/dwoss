package eu.ggnet.dwoss.uniqueunit.eao;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import eu.ggnet.dwoss.uniqueunit.entity.Product;

import com.mysema.query.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.uniqueunit.entity.QProduct.product;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductEao extends AbstractEao<Product> {

    @Inject
    @UniqueUnits
    private EntityManager em;

    public ProductEao() {
        super(Product.class);
    }

    public ProductEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns a Product by partNo.
     * <p>
     * @param partNo the partNo
     * @return Product by partNo, may be null.
     */
    public Product findByPartNo(String partNo) {
        return new JPAQuery(em).from(product).where(product.partNo.eq(partNo)).singleResult(product);
    }

    public List<Product> findByPartNos(Collection<String> partNos) {
        if ( partNos == null || partNos.isEmpty() ) return new ArrayList<>();
        return em.createNamedQuery("Product.byPartNos", Product.class).setParameter(1, partNos).getResultList();
    }

    /**
     * Returns all products which match the supplied tradenames.
     * <p>
     * @param tradenames the tradenames.
     * @return all products which match the supplied tradenames.
     */
    public List<Product> findByTradeNames(Collection<TradeName> tradenames) {
        if ( tradenames == null || tradenames.isEmpty() ) return new ArrayList<>();
        return em.createNamedQuery("Product.byTradeNames", Product.class).setParameter(1, tradenames).getResultList();
    }

    /**
     * Return all UniqueUnits which have the supplied contractor.
     * <p/>
     * @param contractor the contractor.
     * @return all UniqueUnits which have the supplied contractor.
     */
    public List<Product> findByContractor(TradeName contractor) {
        return em.createNamedQuery("Product.byContractor", Product.class).setParameter(1, contractor).getResultList();
    }
}
