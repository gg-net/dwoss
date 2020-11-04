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

import java.util.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.User;
import eu.ggnet.dwoss.rights.api.UserApi;
import eu.ggnet.dwoss.rights.api.Group;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.ee.op.PasswordUtil;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QOperator.operator;

import eu.ggnet.dwoss.rights.api.PreAuthenticationHook;

/**
 * Implementation of {@link UserApi}.
 *
 * @author mirko.schulze
 */
@Stateless
@LocalBean
@AutoLogger
public class UserApiBean implements UserApi {

    @Inject
    @Rights
    private EntityManager em;
    
    private final static Logger L = LoggerFactory.getLogger(UserApiBean.class);

    
    @Inject
    private Instance<PreAuthenticationHook> service;
    
    @Override
    public User authenticate(String username, char[] password) throws UserInfoException {
        L.info("login(user={}, password=xxxxxxx) requested", username);
        //find users by Username
        if ( username == null ) throw new UserInfoException("Kein Username angegeben");
        if ( password == null || password.length == 0 ) throw new UserInfoException("Kein Password angegeben");
        
        Operator op = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( op == null ) throw new UserInfoException("User " + username + " existiert nicht");
        if ( !service.isAmbiguous() && !service.isUnsatisfied() ) {
            if ( service.get().authenticate(username, password) ) {
                L.info("login(user={}, password=xxxxxxx) via AuthenticationService successful.", username);
                return findByName(username);
            }
        } else {
            if ( op.getPassword() != null && op.getSalt() != null
                    && Arrays.equals(op.getPassword(), PasswordUtil.hashPassword(password, op.getSalt())) ) {
                L.info("login(user={}, password=xxxxxxx) via internal database successful.", username);
                return findByName(username);
            }

        }
        L.warn("login(user={}, password=xxxxxxx) failed.", username);
        throw new UserInfoException("Authentifizierung nicht gelungen!");
    }

    @Override
    public int getQuickLoginKey(long id) throws IllegalArgumentException {
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.id.eq(id)).fetchOne();
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with id " + id + ".");
        }
        return user.getQuickLoginKey();
    }

    @Override
    public User create(String username) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        em.persist(new Operator(username));
        return findByName(username);
    }

    @Override
    public User updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        user.setUsername(username);
        return findById(userId);
    }

    @Override
    public User addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to User " + user.getUsername() + ".");
        }
        user.add(right);
        return findById(userId);
    }

    @Override
    public User removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( !user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to User " + user.getUsername() + " at all.");
        }
        user.getRights().remove(right);
        return findById(userId);
    }

    @Override
    public User addGroup(long userId, long groupId) throws IllegalArgumentException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if ( user.getPersonas().contains(group) ) {
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " is already associated with User " + user.getUsername() + ".");
        }
        user.getPersonas().add(group);
        return findById(userId);
    }

    @Override
    public User removeGroup(long userId, long groupId) throws IllegalArgumentException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if ( !user.getPersonas().contains(group) ) {
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " wasn't associated with User " + user.getUsername() + " at all.");
        }
        user.getPersonas().remove(group);
        return findById(userId);
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
    public User findById(long id) throws IllegalArgumentException {
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.id.eq(id)).fetchOne();
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with id " + id + ".");
        }
        List<Persona> personas = user.getPersonas();
        List<Group> groups = new ArrayList<>();
        personas.forEach(g -> {
            groups.add(new Group.Builder()
                    .setId(Optional.of(g.getId()))
                    .setName(g.getName())
                    .setOptLock(Optional.of(g.getOptLock()))
                    .addAllRights(g.getPersonaRights())
                    .build());
        });
        return new User.Builder()
                .setId(Optional.of(user.getId()))
                .setUsername(user.getUsername())
                .setOptLock(Optional.of(user.getOptLock()))
                .addAllGroups(groups)
                .addAllRights(user.getRights())
                .build();
    }

    @Override
    public User findByName(String username) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Username is null.");
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with name " + username + ".");
        }
        List<Persona> personas = user.getPersonas();
        List<Group> groups = new ArrayList<>();
        personas.forEach(g -> {
            groups.add(new Group.Builder()
                    .setId(Optional.of(g.getId()))
                    .setName(g.getName())
                    .setOptLock(Optional.of(g.getOptLock()))
                    .addAllRights(g.getPersonaRights())
                    .build());
        });
        return new User.Builder()
                .setId(Optional.of(user.getId()))
                .setUsername(user.getUsername())
                .setOptLock(Optional.of(user.getOptLock()))
                .addAllGroups(groups)
                .addAllRights(user.getRights())
                .build();
    }

    @Override
    public List<User> findAll() {
        List<Operator> operators = new JPAQuery<Operator>(em).from(operator).fetch();
        List<User> users = new ArrayList<>();
        operators.forEach(u -> {
            List<Group> groups = new ArrayList<>();
            u.getPersonas().forEach(g -> {
                groups.add(new Group.Builder()
                        .setId(Optional.of(g.getId()))
                        .setName(g.getName())
                        .setOptLock(Optional.of(g.getOptLock()))
                        .addAllRights(g.getPersonaRights())
                        .build());
            });
            users.add(new User.Builder()
                    .setId(Optional.of(u.getId()))
                    .setUsername(u.getUsername())
                    .setOptLock(Optional.of(u.getOptLock()))
                    .addAllGroups(groups)
                    .addAllRights(u.getRights())
                    .build());

        });
        return users;
    }

}
