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
package eu.ggnet.dwoss.rights.ee;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.core.system.persistence.RemoteAgent;

/**
 * This is the {@link RemoteAgent} for Rights.
 * <p>
 * @author Bastian Venz
 */
@Stateless
@LocalBean
public class RightsAgentBean extends AbstractAgentBean implements RightsAgent {

    private final static Logger L = LoggerFactory.getLogger(RightsAgentBean.class);

    @Inject
    @Rights
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Persona store(Persona p) {
        L.info("Storing {}", p);
        if ( p.getId() == 0 ) {
            em.persist(p);
            return p;
        }
        return em.merge(p);
    }

    @Override
    public Operator store(Operator o) {
        L.info("Storing {}", o);
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
