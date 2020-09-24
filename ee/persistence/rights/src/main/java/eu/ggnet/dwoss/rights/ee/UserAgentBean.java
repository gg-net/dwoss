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

import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.eao.OperatorEao;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

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
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(username, "Submitted username is null.");
        if(username.isBlank()){
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        user.setUsername(username);
    }
    
    @Override
    public void updatePassword(long userId, byte[] password) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if(user == null){
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(password, "Submitted password is null.");
        if(password.length == 0){
            throw new IllegalArgumentException("Submitted password is empty.");
        }
        user.setPassword(password);
    }

    @Override
    public void updateQuickLoginKey(long userId, int quickLoginKey) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if(user == null){
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(quickLoginKey, "Submitted quickLoginKey is null.");
        user.setQuickLoginKey(quickLoginKey);
    }

    @Override
    public void delete(long userId) throws IllegalArgumentException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        em.remove(user);
    }

    @Override
    public void addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to User " + user.getUsername() + ".");
        }
        user.add(right);
    }

    @Override
    public void removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( !user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to User " + user.getUsername() + " at all.");
        }
        user.getRights().remove(right);
    }

    @Override
    public void addGroup(long userId, long groupId) throws IllegalArgumentException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null){
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = em.find(Persona.class, groupId);
        if(group == null){
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if(user.getPersonas().contains(group)){
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " is already associated with User " + user.getUsername() + ".");
        }
        user.getPersonas().add(group);
    }

    @Override
    public void removeGroup(long userId, long groupId) throws IllegalArgumentException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null){
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = em.find(Persona.class, groupId);
        if(group == null){
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if(!user.getPersonas().contains(group)){
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " wasn't associated with User " + user.getUsername() + " at all.");
        }
        user.getPersonas().remove(group);
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
