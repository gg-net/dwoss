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
package eu.ggnet.dwoss.rights.api;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QOperator.operator;

/**
 *
 * @author mirko.schulze
 */
@Stateless
public class UserApiBean implements UserApi {

    @Inject
    @Rights
    private EntityManager em;

    @Override
    public int getQuickLoginKey(long id) throws IllegalArgumentException {
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.id.eq(id)).fetchOne();
        if ( user == null ) throw new IllegalArgumentException("No User found with id " + id + ".");
        return user.getQuickLoginKey();
    }

    @Override
    public boolean authenticate(String username, byte[] password) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Submitted username is null.");
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( user == null ) throw new IllegalArgumentException("No User found with name " + username + ".");
        return user.getPassword() == password;
    }

    @Override
    public User findById(long id) throws IllegalArgumentException {
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.id.eq(id)).fetchOne();
        if ( user == null ) throw new IllegalArgumentException("No User found with id " + id + ".");
        List<Persona> personas = user.getPersonas();
        List<Group> groups = new ArrayList<>();
        personas.forEach(p -> {
            groups.add(new Group.Builder()
                    .setId(p.getId())
                    .setName(p.getName())
                    .setOptLock(p.getOptLock())
                    .addAllRights(p.getPersonaRights())
                    .build());
        });
        return new User.Builder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setOptLock(user.getOptLock())
                .addAllGroups(groups)
                .addAllRights(user.getRights())
                .build();

    }

    @Override
    public User findByName(String username) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(username, "Username is null.");
        Operator user = new JPAQuery<Operator>(em).from(operator).where(operator.username.eq(username)).fetchOne();
        if ( user == null ) throw new IllegalArgumentException("No User found with name " + username + ".");
        List<Persona> personas = user.getPersonas();
        List<Group> groups = new ArrayList<>();
        personas.forEach(p -> {
            groups.add(new Group.Builder()
                    .setId(p.getId())
                    .setName(p.getName())
                    .setOptLock(p.getOptLock())
                    .addAllRights(p.getPersonaRights())
                    .build());
        });
        return new User.Builder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setOptLock(user.getOptLock())
                .addAllGroups(groups)
                .addAllRights(user.getRights())
                .build();
    }

    @Override
    public List<User> findAll() {
        List<Operator> operators = new JPAQuery<Operator>(em).from(operator).fetch();
        List<User> users = new ArrayList<>();
        operators.forEach(o -> {
            List<Group> groups = new ArrayList<>();
            o.getPersonas().forEach(p -> {
                groups.add(new Group.Builder()
                        .setId(p.getId())
                        .setName(p.getName())
                        .setOptLock(p.getOptLock())
                        .addAllRights(p.getPersonaRights())
                        .build());
            });
            users.add(new User.Builder()
                    .setId(o.getId())
                    .setUsername(o.getUsername())
                    .setOptLock(o.getOptLock())
                    .addAllGroups(groups)
                    .addAllRights(o.getRights())
                    .build());

        });
        return users;
    }

}
