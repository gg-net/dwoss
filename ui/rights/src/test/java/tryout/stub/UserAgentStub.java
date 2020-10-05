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

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.UserAgent;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

public class UserAgentStub implements UserAgent {
    
    private static final Logger L = LoggerFactory.getLogger(UserAgentStub.class);

    private static Map<Long, Operator> usersByIds = new HashMap<>();

    private static Map<Long, Persona> groupsByIds = new HashMap<>();

    public static Map<Long, Operator> getUsersByIds() {
        return usersByIds;
    }

    public static Map<Long, Persona> getGroupsByIds() {
        return groupsByIds;
    }

    public UserAgentStub() {
        L.info("Entering UserAgentStub constructor");
        for (int i = 1; i < 4; i++) {
            Persona group = new Persona(i, 0, "Gruppe " + i, getRandomRights());
            groupsByIds.put(group.getId(), group);
            L.info("constructor: Added Group {}", group);
        }
        for (int j = 0; j < 3; j++) {
            int till = (int)(Math.random() * 3 - 1);
            List<Persona> groups = new ArrayList<>(groupsByIds.values()).subList(0, till);
            Operator user = new Operator(j, 0, 0, "Benutzer " + j, RandomStringUtils.randomAlphanumeric(6).getBytes(),
                    RandomStringUtils.randomAlphanumeric(5).getBytes(), groups, getRandomRights());
            usersByIds.put(user.getId(), user);
            L.info("constructor: Added User {}", user);
        }
        L.info("Exiting UserAgentStub construsctor");
    }

    @Override
    public void create(String username) throws IllegalArgumentException, NullPointerException {
        L.info("Entering create({})", username);
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        Operator user = new Operator(username);
        usersByIds.put(user.getId(), user);
        L.info("create(): added new User {}", user);
    }

    @Override
    public void updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException {
        L.info("Entering updateUsername({}, {})", userId, username);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        user.setUsername(username);
        L.info("updateUsername(): set username to {}", user.getUsername());
    }

    @Override
    public void updatePassword(long userId, byte[] password) throws IllegalArgumentException, NullPointerException {
        L.info("Entering updatePassword({}, {})", userId, password);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(password, "Submitted password is null.");
        if ( password.length == 0 ) {
            throw new IllegalArgumentException("Submitted password is empty.");
        }
        user.setPassword(password);
        L.info("updatePassword(): set password to {}", user.getPassword());
    }

    @Override
    public void updateQuickLoginKey(long userId, int quickLoginKey) throws IllegalArgumentException {
        L.info("Entering updateQuickLoginKey({}, {})", userId, quickLoginKey);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        user.setQuickLoginKey(quickLoginKey);
        L.info("updateQuickLoginKey(): set quickLoginKey to {}", user.getQuickLoginKey());
    }

    @Override
    public void addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.info("Entering addRight({}, {})", userId, right);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to User " + user.getUsername() + ".");
        }
        user.add(right);
        L.info("addRight(): added Right {} to User {}", right, user);
    }

    @Override
    public void removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
        L.info("Entering removeRight({}, {})", userId, right);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( !user.getRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to User " + user.getUsername() + " at all.");
        }
        user.getRights().remove(right);
        L.info("removeRight(): removed Right {} from User {}", right, user);
    }

    @Override
    public void addGroup(long userId, long groupId) throws IllegalArgumentException {
        L.info("Entering addGroup({}, {})", userId, groupId);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = groupsByIds.get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if ( user.getPersonas().contains(group) ) {
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " is already associated with User " + user.getUsername() + ".");
        }
        user.getPersonas().add(group);
        L.info("addGroup(): added Group {} to User {}", group, user);
    }

    @Override
    public void removeGroup(long userId, long groupId) throws IllegalArgumentException {
        L.info("Entering removeGroup({}, {})", userId, groupId);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId " + userId + ".");
        }
        Persona group = groupsByIds.get(groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId " + groupId + ".");
        }
        if ( !user.getPersonas().contains(group) ) {
            throw new IllegalArgumentException("Submitted Group " + group.getName() + " wasn't associated with User " + user.getUsername() + " at all.");
        }
        user.getPersonas().remove(group);
        L.info("removeGroup(): removed Group {} from User {}", group, user);
    }

    @Override
    public void delete(long userId) throws IllegalArgumentException {
        L.info("Entering delete({})", userId);
        Operator user = usersByIds.get(userId);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + userId + ".");
        }
        usersByIds.remove(user.getId());
        L.info("delete(): deleted User {}", user);
    }

    @Override
    public Operator findByName(String username) throws IllegalArgumentException, NullPointerException {
        L.info("Entering findByName({})", username);
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        Operator user = usersByIds.values().stream().filter(u -> u.getUsername().equals(username)).findAny().orElseGet(() -> null);
        L.info("findByName(): returning User {}", user);
        return user;
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        L.info("Entering count({})", entityClass);
        int count = usersByIds.size();
        L.info("count(): returning {}", count);
        return count;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        L.info("Entering findAll({})", entityClass);
        List<T> findAll = (List<T>)new ArrayList<>(usersByIds.values());
        L.info("findAll(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        L.info("Entering findAll({}, {}, {})", entityClass, start, amount);
        List<T> findAll = findAll(entityClass);
        L.info("findAll(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        L.info("Entering findAllEager({})", entityClass);
        List<T> findAll = findAll(entityClass);
        L.info("findAllEager(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        L.info("Entering findAllEager({}, {}, {})", entityClass, start, amount);
        List<T> findAll = findAll(entityClass);
        L.info("findAllEager(): returning {}", findAll);
        return findAll;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        L.info("Entering findById({}, {})", entityClass, id);
        Long userId = (Long)id;
        Operator user = usersByIds.get(userId);
        L.info("findById(): returning {}", user);
        return (T)user;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        L.info("Entering findById({}, {}, {})", entityClass, id, lockModeType);
        T findById = findById(entityClass, id);
        L.info("findById(): returning {}", findById);
        return findById;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        L.info("Entering findByIdEager({}, {})", entityClass, id);
        T findById = findById(entityClass, id);
        L.info("findByIdEager(): returning {}", findById);
        return findById;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        L.info("Entering findByIdEager({}, {}, {})", entityClass, id, lockModeType);
        T findById = findById(entityClass, id);
        L.info("findByIdEager(): returning {}", findById);
        return findById;
    }

    private List<AtomicRight> getRandomRights() {
        L.info("Entering getRandomRights()");
        List<AtomicRight> rights = Arrays.asList(AtomicRight.values());
        Collections.shuffle(rights);
        int till = (int)(Math.random() * rights.size() - 1) + 1;
        rights = rights.subList(0, till);
        L.info("getRandomRights(): returning List of Rights {}", rights);
        return rights;
    }

}
