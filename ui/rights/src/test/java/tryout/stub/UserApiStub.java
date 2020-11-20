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

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.ee.op.PasswordUtil;

/**
 * Stub implementation of {@link UserApi} for testing purposes.
 *
 * @author mirko.schulze
 */
public class UserApiStub implements UserApi {

    private static final Logger L = LoggerFactory.getLogger(UserApiStub.class);

    private static final Map<Long, Operator> usersByIds = new HashMap<>();

    private static final Map<Long, Persona> groupsByIds = new HashMap<>();

    private final String password = "qwert";

    private int userId = 1;

    private static int groupId = 1;

    public static Map<Long, Operator> getUsersByIds() {
        return usersByIds;
    }

    public static Map<Long, Persona> getGroupsByIds() {
        return groupsByIds;
    }

    public static int getGroupId() {
        return groupId;
    }

    public static void incrementGroupId() {
        groupId++;
    }

    public UserApiStub() {
        L.info("Entering UserApiStub constructor");
        for (int i = 1; i < 4; i++) {
            Persona group = new Persona(groupId, 0, "Gruppe " + i, getRandomRights());
            groupsByIds.put(group.getId(), group);
            groupId++;
            L.info("constructor: Added Group {}", group);
        }
        for (int j = 1; j < 4; j++) {
            int endIndex = (int)(Math.random() * 3 - 1);
            List<Persona> groups = new ArrayList<>(groupsByIds.values()).subList(0, endIndex);
            Operator user = new Operator(userId, 0, 0, "Benutzer " + j, RandomStringUtils.randomAlphanumeric(6).getBytes(),
                    RandomStringUtils.randomAlphanumeric(5).getBytes(), groups, getRandomRights());
            usersByIds.put(user.getId(), user);
            userId++;
            L.info("constructor: Added User {}", user);
        }
        L.info("Exiting UserApiStub construsctor");
    }

    @Override
    public User authenticate(String username, char[] password) throws UserInfoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getQuickLoginKey(long id) throws IllegalArgumentException {
        L.info("Entering getQuickLoginKey({})", id);
        Operator user = usersByIds.get(id);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with userId = " + id + ".");
        }
        return user.getQuickLoginKey();
    }

    @Override
    public User create(String username) throws IllegalArgumentException, NullPointerException {
        L.info("Entering create({})", username);
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        Operator user = new Operator(userId, 0, 0, username, null, null, new ArrayList<>(), new ArrayList<>());

        usersByIds.put(user.getId(), user);
        userId++;
        L.info("create(): added new User {}", user);

        return user.toApiUser();
    }

    @Override
    public User updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException {
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
        User u = user.toApiUser();
        L.info("updateUsername(): set username to {}", user.getUsername());
        return u;
    }

    @Override
    public User updatePassword(long userId, char[] password) throws IllegalArgumentException, NullPointerException {
        L.info("Entering updatePassword({}, {})", userId, password);
        Operator user = usersByIds.get(userId);
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
        User u = user.toApiUser();
        L.info("updatePassword(): set password to {}", user.getPassword());
        return u;
    }

    @Override
    public User addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
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
        User u = user.toApiUser();
        L.info("addRight(): added Right {} to User {}", right, user);
        return u;
    }

    @Override
    public User removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException {
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
        User u = user.toApiUser();
        L.info("removeRight(): removed Right {} from User {}", right, user);
        return u;
    }

    @Override
    public User addGroup(long userId, long groupId) throws IllegalArgumentException {
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
        User u = user.toApiUser();
        L.info("addGroup(): added Group {} to User {}", group, user);
        return u;
    }

    @Override
    public User removeGroup(long userId, long groupId) throws IllegalArgumentException {
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
        User u = user.toApiUser();
        L.info("removeGroup(): removed Group {} from User {}", group, user);
        return u;
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
    public User findById(long id) throws IllegalArgumentException {
        L.info("Entering findById({})", id);
        Operator user = usersByIds.get(id);
        User u = user.toApiUser();
        L.info("findById(): returning User {}", u);
        return u;
    }

    @Override
    public User findByName(String username) throws IllegalArgumentException, NullPointerException {
        L.info("Entering findByName({})", username);
        Objects.requireNonNull(username, "Submitted username is null.");
        if ( username.isBlank() ) {
            throw new IllegalArgumentException("Submitted username is blank.");
        }
        Operator user = usersByIds.values().stream().filter(u -> u.getUsername().equals(username)).findAny().orElseGet(() -> null);
        if ( user == null ) {
            throw new IllegalArgumentException("No User found with username = " + username + ".");
        }
        User u = user.toApiUser();
        L.info("findByName(): returning User {}", u);
        return u;
    }

    @Override
    public List<User> findAll() {
        L.info("Entering findAll()");
        List<Operator> users = new ArrayList<>(usersByIds.values());
        List<User> findAll = new ArrayList<>();
        users.forEach(u -> findAll.add(u.toApiUser()));
        L.info("findAll(): returning {}", findAll);
        return findAll;
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
