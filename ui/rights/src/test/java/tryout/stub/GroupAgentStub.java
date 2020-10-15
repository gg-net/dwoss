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

import javax.persistence.LockModeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.GroupAgent;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

public class GroupAgentStub implements GroupAgent {
    
    private static final Logger L = LoggerFactory.getLogger(UserAgentStub.class);

    @Override
    public void create(String name) throws IllegalArgumentException, NullPointerException {
        L.debug("Entering create({})", name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( isNameAlreadyInUsage(name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        Persona group = new Persona(name);
        UserAgentStub.getGroupsByIds().put(group.getId(), group);
        L.debug("create()): added new Group {}", group);
    }

    @Override
    public void updateName(long groupId, String name) throws IllegalArgumentException, NullPointerException {
        L.debug("Entering updateName({}, {})", groupId, name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        Persona group = UserAgentStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( isNameAlreadyInUsage(name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        group.setName(name);
        L.debug("updateName(): set name to {}", name);
    }

    @Override
    public void delete(long groupId) throws IllegalArgumentException {
        L.debug("Entering delete({})", groupId);
        Persona group = UserAgentStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        UserAgentStub.getGroupsByIds().remove(group.getId());
        L.debug("delete(): deleted Group {}", group);
    }

    @Override
    public void addRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.debug("Entering addRight({}, {})", groupId, right);
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = UserAgentStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to Group " + group.getName() + ".");
        }
        group.add(right);
        L.debug("addRight(): added Right {} to Group {}", right, group);
    }

    @Override
    public void removeRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.debug("Entering removeRight({}, {})", groupId, right);
        Objects.requireNonNull(right, "Right must not be null.");
        Persona group = UserAgentStub.getGroupsByIds().get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId + ".");
        }
        if ( !group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to Group " + group.getName() + " at all.");
        }
        group.getPersonaRights().remove(right);
        L.debug("removeRight(): removed Right {} from Group {}", right, group);
    }

    @Override
    public Persona findByName(String name) throws IllegalArgumentException, NullPointerException {
        L.debug("Entering findByName({})", name);
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        Persona group = UserAgentStub.getGroupsByIds().values().stream().filter(u -> u.getName().equals(name)).findAny().orElseGet(() -> null);
        L.debug("findByName(): returning Group {}", group);
        return group;
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        L.debug("Entering count({})", entityClass);
        int count = UserAgentStub.getGroupsByIds().size();
        L.debug("count(): returning {}", count);
        return count;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        L.debug("Entering findAll({})", entityClass);
        List<T> findAll = (List<T>)new ArrayList<>(UserAgentStub.getGroupsByIds().values());
        L.debug("findAll(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        L.debug("Entering findAll({}, {}, {})", entityClass, start, amount);
        List<T> findAll = findAll(entityClass);
        L.debug("findAll(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        L.debug("Entering findAllEager({})", entityClass);
        List<T> findAll = findAll(entityClass);
        L.debug("findAllEager(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        L.debug("Entering findAllEager({}, {}, {})", entityClass, start, amount);
        List<T> findAll = findAll(entityClass);
        L.debug("findAllEager(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        L.debug("Entering findById({}, {})", entityClass, id);
        Long groupId = (Long)id;
        Persona group = UserAgentStub.getGroupsByIds().get(groupId);
        L.debug("findById(): returning {}", group);
        return (T)group;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        L.debug("Entering findById({}, {}, {})", entityClass, id, lockModeType);
        T findById = findById(entityClass, id);
        L.debug("findById(): returning {}", findById);
        return findById;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        L.debug("Entering findByIdEager({}, {})", entityClass, id);
        T findById = findById(entityClass, id);
        L.debug("findByIdEager(): returning {}", findById);
        return findById;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        L.debug("Entering findByIdEager({}, {}, {})", entityClass, id, lockModeType);
        T findById = findById(entityClass, id);
        L.debug("findByIdEager(): returning {}", findById);
        return findById;
    }

    private boolean isNameAlreadyInUsage(String name) {
        List<Persona> allGroups = findAll(Persona.class);
        return allGroups.stream().anyMatch(g -> g.getName().equals(name));
    }

}
