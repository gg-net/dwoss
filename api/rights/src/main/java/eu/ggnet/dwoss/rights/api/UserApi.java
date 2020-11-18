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

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 * Interface for the persistence layer of the Rights module for {@link Operator} entitites.
 * <p/>
 * Contains methods to create, update or delete Operators, to add or remove an {@link AtomicRight} to/from a User, and to add or remove a {@link Persona}
 * to/from a User.
 *
 * @author oliver.guenther
 */
@Remote
public interface UserApi {

    /**
     * Searches for an {@link Operator} with the submitted username in the database and compares the password of that Operator with the submitted password.
     * <p/>
     * Returns true if both passwords match.
     *
     * @param username name of the Operator, must not be null or blank.
     * @param password password of the Operator, must not be null or empty.
     * @return User - representation the Operator, if the submitted password matches the password of the Operator with the submitted username.
     * @throws UserInfoException <ol><li>if the submitted username is null or blank.</li>
     * <li>if the submitted password is null or empty.</li>
     * <li>if no Operator with the submitted username exists.
     * <li>if the User can not be authenticated.</li></ol>
     */
    User authenticate(String username, char[] password) throws UserInfoException;

    /**
     * Returns the quickLoginKey of the {@link Operator} with the submitted userId.
     *
     * @param userId id of the Operator, must exist in the database.
     * @return int - the quickLoginKey of the Operator.
     * @throws IllegalArgumentException if no Operator with the submitted userId exists.
     */
    int getQuickLoginKey(long userId) throws IllegalArgumentException;

    /**
     * Creates a new {@link Operator} in the database and returns a {@link User} representation of that Operator.
     *
     * @param username name for the new Operator, must not be null or blank.
     * @return User - representation the new Operator.
     * @throws IllegalArgumentException if the submitted username is blank.
     * @throws NullPointerException     if the submitted username is null.
     */
    User create(String username) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted name as new name at the {@link Operator} with the submitted userId and returns a {@link User} representation of that Operator.
     *
     * @param userId   id of the Operator, must exist in the database.
     * @param username new name for the Operator, must not be null or blank.
     * @return User - representation of the Operator that was modified.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if the submitted name is blank.</li></ol>
     * @throws NullPointerException     if the submitted username is null.
     */
    User updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted password as new password at the {@link Operator} with the submitted userId and returns a {@link User} representation of that Operator.
     *
     * @param userId   id of the Operator, must exist in the database.
     * @param password new password for the Operator, must not be null or empty.
     * @return User - representation of the Operator that was modified.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if the submitted password is empty.</li></ol>
     * @throws NullPointerException     if the submitted password is null.
     */
    User updatePassword(long userId, char[] password) throws IllegalArgumentException, NullPointerException;

    /**
     * Adds the submitted {@link AtomicRight} to the {@link Operator} with the submitted userId and returns a {@link User} representation of that Operator.
     *
     * @param userId id of the Operator, must exist in the database.
     * @param right  AtomicRight to be added, must not be null.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if the submitted AtomicRight is already granted to the Operator.</li></ol>
     * @throws NullPointerException     if the submitted AtomicRight is null.
     */
    User addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Removes the submitted {@link AtomicRight} from the {@link Operator} with the submitted userId and returns a {@link User} representation of that Operator.
     *
     * @param userId id of the Operator, must exist in the database.
     * @param right  AtomicRight to be removed, must not be null.
     * @return User - representation of the Operator that was modified.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if the submitted AtomicRight wasn't granted to the Operator at all.</li></ol>
     * @throws NullPointerException     if the submitted AtomicRight is null.
     */
    User removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Adds the {@link Persona} with the submitted groupId to the {@link Operator} with the submitted userId and returns a {@link User} representation of that
     * Operator.
     *
     * @param userId  id if the Operator, must exist in the database.
     * @param groupId id of the Persona, must exist in the database.
     * @return User - representation of the Operator that was modified.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if no Persona with the submitted groupId exists.</li>
     * <li>if the Persona is already associated with the Operator.</li></ol>
     */
    User addGroup(long userId, long groupId) throws IllegalArgumentException;

    /**
     * Removes the {@link Persona} with the submitted groupId from the {@link Operator} with the submitted userId and returns a {@link User} representation of
     * that Operator.
     *
     * @param userId  id if the Operator, must exist in the database.
     * @param groupId id of the Persona, must exist in the database.
     * @return User - representation of the Operator that was modified.
     * @throws IllegalArgumentException <ol><li>if no Operator with the submitted userId exists.</li>
     * <li>if no Persona with the submitted groupId exists.</li>
     * <li>if the Persona wasn't associated with the Operator at all.</li></ol>
     */
    User removeGroup(long userId, long groupId) throws IllegalArgumentException;

    /**
     * Deletes the {@link Operator} with the submitted userId from the database.
     *
     * @param userId id of the Operator to be deleted, must exist in the database.
     * @throws IllegalArgumentException if no Operator with the submitted userId exists.
     */
    void delete(long userId) throws IllegalArgumentException;

    /**
     * Searches for the {@link Operator} with the submitted userId and returns a {@link User} representation of that Operator.
     *
     * @param userId id of the Operator, must exist in the database.
     * @return User - representation of the found Operator or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    User findById(long userId) throws IllegalArgumentException;

    /**
     * Searches for the {@link Operator} with the submitted username and returns a {@link User} representation of that Operator.
     *
     * @param username name of the Operator, must not be null or blank.
     * @return User - representation of the found Operator or null.
     * @throws IllegalArgumentException if the submitted name is blank or the user is not found.
     * @throws NullPointerException     if the submitted name is null.
     */
    User findByName(String username) throws IllegalArgumentException, NullPointerException;

    /**
     * Searches for all {@link Operator}<code>s</code> and returns a List of {@link User} with representations of those Operators.
     *
     * @return List&lt;User&gt; - representations of the found Operators or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    List<User> findAll();
}
