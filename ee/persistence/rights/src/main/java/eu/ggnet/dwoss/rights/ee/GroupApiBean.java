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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QPersona.persona;

/**
 *
 * @author mirko.schulze
 */
@Stateless
@AutoLogger
public class GroupApiBean implements GroupApi {

    @Inject
    @Rights
    private EntityManager em;

    @Override
    public Group create(String name) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( isNameAlreadyUsedByAnotherGroup(-1, name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        em.persist(new Persona(name));
        return findByName(name);
    }

    @Override
    public Group updateName(long groupId, String name) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(name, "Submitted name is null.");
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( isNameAlreadyUsedByAnotherGroup(groupId, name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        group.setName(name);
        return findById(groupId);
    }

    @Override
    public Group addRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to Group " + group.getName() + ".");
        }
        group.add(right);
        return findById(groupId);
    }

    @Override
    public Group removeRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( !group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to Group " + group.getName() + " at all.");
        }
        group.getPersonaRights().remove(right);
        return findById(groupId);
    }

    @Override
    public void delete(long groupId) throws IllegalArgumentException {
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        em.remove(group);
    }

    @Override
    public Group findById(long groupId) throws IllegalArgumentException {
        Persona group = new JPAQuery<Persona>(em).from(persona).where(persona.id.eq(groupId)).fetchOne();
        if ( group == null ) throw new IllegalArgumentException("No Group found with id " + groupId + ".");
        return new Group.Builder()
                .setId(Optional.of(group.getId()))
                .setName(group.getName())
                .setOptLock(Optional.of(group.getOptLock()))
                .addAllRights(group.getPersonaRights())
                .build();
    }

    @Override
    public Group findByName(String name) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(name, "Submitted name is null,");
        Persona group = new JPAQuery<Persona>(em).from(persona).where(persona.name.eq(name)).fetchOne();
        if ( group == null ) throw new IllegalArgumentException("No Group found with name " + name + ".");
        return new Group.Builder()
                .setId(Optional.of(group.getId()))
                .setName(group.getName())
                .setOptLock(Optional.of(group.getOptLock()))
                .addAllRights(group.getPersonaRights())
                .build();
    }

    @Override
    public List<Group> findAll() {
        List<Persona> personas = new JPAQuery<Persona>(em).from(persona).fetch();
        List<Group> groups = new ArrayList<>();
        personas.forEach(g -> {
            groups.add(new Group.Builder()
                    .setId(g.getId())
                    .setName(g.getName())
                    .setOptLock(g.getOptLock())
                    .addAllRights(g.getPersonaRights()).build());
        });
        return groups;
    }

    private boolean isNameAlreadyUsedByAnotherGroup(long groupId, String name) {
        Persona group = new JPAQuery<Persona>(em).from(persona).where(persona.id.eq(groupId)).fetchOne();
        if ( group != null ) {
            if ( group.getName().equals(name) ) return false;
        }
        List<Group> allGroups = findAll();
        return allGroups.stream().anyMatch(g -> g.getName().equals(name));
    }

}
