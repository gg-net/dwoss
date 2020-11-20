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

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.User;
import eu.ggnet.dwoss.rights.api.UserApi;
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

    private final static Logger L = LoggerFactory.getLogger(UserApiBean.class);

    @Inject
    @Rights
    private EntityManager em;

    @Inject
    private Instance<PreAuthenticationHook> service;

    @Override
    public User authenticate(String username, char[] password) throws UserInfoException {
        L.info("login({}, xxxxxxx) called", username);
        //find users by Username
        if ( username == null || username.isBlank() ) throw new UserInfoException("Kein Nutzername angegeben.");
        if ( password == null || password.length == 0 ) throw new UserInfoException("Kein Passwort angegeben.");

        Operator op = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( op == null ) throw new UserInfoException("Benutzer " + username + " existiert nicht.");

        if ( !service.isAmbiguous() && !service.isUnsatisfied() ) {
            if ( service.get().authenticate(username, password) ) {
                L.info("login() via AuthenticationService successful");
                return op.toApiUser();
            }
        } else {
            if ( op.getPassword() != null && op.getSalt() != null
                    && Arrays.equals(op.getPassword(), PasswordUtil.hashPassword(password, op.getSalt())) ) {
                L.info("login() via internal database successful");
                return op.toApiUser();
            }
        }
        L.warn("login() failed", username);
        throw new UserInfoException("Authentifizierung nicht gelungen!");
    }

    @Override
    public int getQuickLoginKey(long id) throws IllegalArgumentException {
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.id.eq(id)).fetchOne();
        if ( user == null ) {
            L.info("No User found with id {}.", user, id);
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
        return user.toApiUser();
    }

    @Override
    public User updatePassword(long userId, char[] password) throws IllegalArgumentException, NullPointerException {
        Operator user = em.find(Operator.class, userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(password, "Submitted password is null.");
        if ( password.length == 0 ) {
            throw new IllegalArgumentException("Submitted password is empty.");
        }

        byte[] salt = RandomStringUtils.random(30).getBytes();
        byte[] hashPassword = PasswordUtil.hashPassword(password, salt);

        user.setPassword(hashPassword);
        user.setSalt(salt);
        return user.toApiUser();
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
        return user.toApiUser();
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
        return user.toApiUser();
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
        return user.toApiUser();
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
        return user.toApiUser();
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
        User u = user.toApiUser();
        return u;
    }

    @Override
    public User findByName(String username) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Username is null.");
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with name " + username + ".");
        }
        User u = user.toApiUser();
        return u;
    }

    @Override
    public List<User> findAll() {
        List<Operator> operators = new JPAQuery<Operator>(em).from(operator).fetch();
        List<User> users = new ArrayList<>();
        operators.forEach(u -> users.add(u.toApiUser()));
        return users;
    }
}
