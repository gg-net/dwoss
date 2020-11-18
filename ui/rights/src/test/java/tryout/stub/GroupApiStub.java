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
package tryout.stub;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

/**
 * Stub implementation of {@link GroupApi} for testing purposes.
 *
 * @author mirko.schulze
 */
public class GroupApiStub implements GroupApi {

    private static final Logger L = LoggerFactory.getLogger(GroupApiStub.class);

    @Override
    public Group create(String name) throws IllegalArgumentException, NullPointerException {
        L.info("Entering create({})", name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( isNameAlreadyUsedByAnotherGroup(-1, name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        Persona group = new Persona(UserApiStub.getGroupId(), 0, name, new ArrayList<>());
        UserApiStub.getGroupsByIds().put(group.getId(), group);
        UserApiStub.incrementGroupId();
        L.info("create()): added new Group {}", group);

        return findByName(name);
    }

    @Override
    public Group updateName(long groupId, String name) throws IllegalArgumentException, NullPointerException {
        L.info("Entering updateName({}, {})", groupId, name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        Persona group = UserApiStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( isNameAlreadyUsedByAnotherGroup(groupId, name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        group.setName(name);
        L.info("updateName(): set name to {}", name);
        return group.toApiGroup();
    }

    @Override
    public Group addRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.info("Entering addRight({}, {})", groupId, right);
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = UserApiStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to Group " + group.getName() + ".");
        }
        group.add(right);
        L.info("addRight(): added Right {} to Group {}", right, group);
        return group.toApiGroup();
    }

    @Override
    public Group removeRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.info("Entering removeRight({}, {})", groupId, right);
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = UserApiStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( !group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to Group " + group.getName() + " at all.");
        }
        group.getPersonaRights().remove(right);
        L.info("removeRight(): removed Right {} from Group {}", right, group);
        return group.toApiGroup();
    }

    @Override
    public void delete(long groupId) throws IllegalArgumentException {
        L.info("Entering delete({})", groupId);
        Persona group = UserApiStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        UserApiStub.getUsersByIds().values().forEach(u -> {
            if ( u.getPersonas().contains(group) ) {
                UserApiStub.getUsersByIds().get(u.getId()).getPersonas().remove(group);
            }
        });
        UserApiStub.getGroupsByIds().remove(group.getId());
        L.info("delete(): deleted Group {}", group);
    }

    @Override
    public Group findById(long id) throws IllegalArgumentException {
        L.info("Entering findById({})", id);
        Persona group = UserApiStub.getGroupsByIds().get(id);
        L.info("found group = {}", group);

        Group g = group.toApiGroup();
        L.info("findById(): returning {}", g);
        return g;
    }

    @Override
    public Group findByName(String name) throws IllegalArgumentException, NullPointerException {
        L.info("Entering findByName({})", name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        Persona group = UserApiStub.getGroupsByIds().values().stream().filter(u -> u.getName().equals(name)).findAny().orElseGet(() -> null);

        Group g = group.toApiGroup();
        L.info("findById(): returning {}", g);
        return g;
    }

    @Override
    public List<Group> findAll() {
        L.info("Entering findAll()");
        List<Persona> groups = new ArrayList<>(UserApiStub.getGroupsByIds().values());
        List<Group> findAll = new ArrayList<>();
        groups.forEach(g -> findAll.add(g.toApiGroup()));
        L.info("findAll(): returning {}", findAll);
        return findAll;
    }

    private boolean isNameAlreadyUsedByAnotherGroup(long groupId, String name) {
        Persona group = UserApiStub.getGroupsByIds().get(groupId);
        if ( group != null ) {
            if ( group.getName().equals(name) ) return false;
        }
        List<Group> allGroups = findAll();
        return allGroups.stream().anyMatch(g -> g.getName().equals(name));
    }
}
