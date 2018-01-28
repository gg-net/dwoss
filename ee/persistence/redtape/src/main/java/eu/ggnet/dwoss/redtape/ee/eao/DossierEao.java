/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.redtape.ee.eao;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author pascal.perau
 */
@Stateless
public class DossierEao extends AbstractEao<Dossier> {

    @Inject
    @RedTapes
    private EntityManager em;

    public DossierEao(EntityManager em) {
        super(Dossier.class);
        this.em = em;
    }

    public DossierEao() {
        super(Dossier.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Dossier> findByIds(Collection<Long> dossierIds) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byDossierIds", Dossier.class);
        query.setParameter(1, dossierIds);
        return query.getResultList();
    }

    public List<Dossier> findAllDescending(int start, int end) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.allDescending", Dossier.class);
        query.setFirstResult(start);
        query.setMaxResults(end);
        return query.getResultList();
    }

    public List<Dossier> findByCustomerId(long customerId) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byCustomerId", Dossier.class);
        query.setParameter(1, customerId);
        return query.getResultList();
    }

    /**
     * Returns all Dossiers which have the closed attribute.
     *
     * @param closed the closed attribute
     * @return all Dossiers which have the closed attribute.
     */
    public List<Dossier> findByClosed(boolean closed) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byClosed", Dossier.class);
        query.setParameter(1, closed);
        return query.getResultList();
    }

    /**
     * Get the dossiers where the customerId and the directive matches.
     * <p/>
     *
     * @param customerId the cusotmer id
     * @param start      limites the returned list, this is the start
     * @param amount     limites the returned list, this is the amount of elements to show
     * @return the dossiers where the customerId and the directive matches
     */
    public List<Dossier> findClosedByCustomerId(long customerId, int start, int amount) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byCustomerIdAndClosed", Dossier.class);
        query.setParameter(1, customerId);
        query.setParameter(2, true);
        query.setFirstResult(start);
        query.setMaxResults(amount);
        return query.getResultList();
    }

    /**
     * Get the dossiers where the customerId matches and the directive does not match.
     * <p/>
     *
     * @param customerId the customer id
     * @return the dossiers where the customerId matches and the directive does not match
     */
    public List<Dossier> findOpenByCustomerId(long customerId) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byCustomerIdAndClosed", Dossier.class);
        query.setParameter(1, customerId);
        query.setParameter(2, false);
        return query.getResultList();
    }

    /**
     * Get the dossiers where the identifier matches the search.
     * <p/>
     * @param search the searched identifier
     * @return the dossiers where the identifier matches the search.
     */
    public List<Dossier> findByIdentifier(String search) {
        TypedQuery<Dossier> query = em.createNamedQuery("Dossier.byIdentifier", Dossier.class);
        query.setParameter(1, (search.contains("*") ? search.replace("*", "%") : search.replace("*", "")).toUpperCase());
        return query.getResultList();
    }

    /**
     * Returns true if a uniqueUnit with the supplied id is actually block by a Position->Document->Dossier.
     * This is determined, if a Position of type unit with the uniqueUnitId exists on an active crucial Document which is still open.
     * Normally this is only looked up in the stock. But this can be used as a failsafe if the stock is wrong.
     * <p/>
     * @param uniqueUnitId the uniqueUnitId.
     * @return true if a uniqueUnit with the supplied id is actually block by a Position->Document->Dossier.
     */
    public boolean isUnitBlocked(Integer uniqueUnitId) {
        if ( uniqueUnitId == null ) return false;
        for (Position position : new PositionEao(em).findByUniqueUnitId(uniqueUnitId)) {
            if ( position.getDocument().getDossier().getRelevantUniqueUnitIds().contains(uniqueUnitId) ) return true;
        }
        return false;
    }
}
