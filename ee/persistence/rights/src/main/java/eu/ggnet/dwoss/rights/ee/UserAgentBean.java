/*
 * Copyright (C) 2020 GG-Net GmbH
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

import java.util.Objects;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 * Implementation of {@link UserAgent}.
 *
 * @author mirko.schulze
 */
@Stateless
@LocalBean
public class UserAgentBean extends AbstractAgentBean implements UserAgent {

    @Inject
    @Rights
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @AutoLogger
    @Override
    public void create(String username) {
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        em.persist(new Operator(username));
    }

    @AutoLogger
    @Override
    public void updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if(user == null){
            throw new IllegalArgumentException("No User found with userId = + " + userId + ".");
        }
        Objects.requireNonNull(username, "Submitted username is null.");
        if(username.isBlank()){
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        user.setUsername(username);
    }
    
    @Override
    public void updatePassword(long userId, byte[] password) throws EntityNotFoundException, NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateQuickLoginkey(long userId, int quickLoginKey) throws EntityNotFoundException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(long userId) throws EntityNotFoundException, IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addRight(long userId, AtomicRight right) throws EntityNotFoundException, NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeRight(long userId, AtomicRight right) throws EntityNotFoundException, NullPointerException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addGroup(long userId, long groupId) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeGroup(long userId, long groupId) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @AutoLogger
    @Override
    public Operator findByName(String username) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        Operator user = new OperatorEao(em).findByUsername(username);
        user.fetchEager();
        return user;
    }

}
