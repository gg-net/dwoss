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

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.system.persistence.RemoteAgent;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 * Agent for the persistence layer of the rights module for {@link User} entitites.
 * <p/>
 * Contains methods to create, update or delete Users, to add or remove an {@link AtomicRight} to/from a User, and to add or remove a {@link Group} to/from a
 * User.
 *
 * @author mirko.schulze
 */
@Remote
public interface UserAgent extends RemoteAgent {

    /**
     * Creates a new {@link User}.
     *
     * @param username name for the new User, must not be null or blank.
     * @throws IllegalArgumentException if the submitted username is blank.
     * @throws NullPointerException     if the submitted username is null.
     */
    public void create(String username) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted username as new {@link User#username} at the {@link User} with the submitted userId.
     *
     * @param userId   id of the User, must exist in the database.
     * @param username new username for the User, must not be null or blank.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists</li>
     * <li>if the submitted username is blank.</li></ol>
     * @throws NullPointerException     if the submitted username is null.
     */
    public void updateUsername(long userId, String username) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted password as new {@link User#password} at the {@link User} with the submitted userId.
     *
     * @param userId   id of the User, must exist in the database.
     * @param password new password for the User, must not be null or empty.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists.</li>
     * <li>if the submitted password is empty.</li></ol>
     * @throws NullPointerException     if the submitted password is null.
     */
    public void updatePassword(long userId, byte[] password) throws IllegalArgumentException, NullPointerException;
    //???vorgaben beim password?

    /**
     * Sets the submitted quickLoginKey as new {@link User#quickLoginKey} at the {@link User} with the submitted userId.
     *
     * @param userId        id of the User, must exist in the database.
     * @param quickLoginKey new quickLoginKey for the User.
     * @throws IllegalArgumentException if no User with the submitted userId exists.
     */
    public void updateQuickLoginKey(long userId, int quickLoginKey) throws IllegalArgumentException;
    //??? beschränkungen beim key?

    /**
     * Adds the submitted {@link AtomicRight} to the {@link User} with the sumitted userId.
     *
     * @param userId id of the User, must exist in the database.
     * @param right  Right to be added, must not be null.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists.</li>
     * <li>if the submitted Right is already granted to the User.</li></ol>
     * @throws NullPointerException     if the submitted Right is null.
     */
    public void addRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Removes the submitted {@link AtomicRight} to the {@link User} with the submitted userId.
     *
     * @param userId id of he User, must exist in the database.
     * @param right  Right to be removed must not be null.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists.</li>
     * <li>if the submitted Right wasn't granted to the User at all.</li></ol>
     * @throws NullPointerException     if the submitted Right is null.
     */
    public void removeRight(long userId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Adds the {@link Group} with the submitted groupId to {@link User#groups} at the {@link User} with the submitted userId.
     *
     * @param userId  id if the User, must exist in the database.
     * @param groupId id of the Group, must exist in the database.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists.</li>
     * <li>if no Group with the submitted groupId exists.</li>
     * <li>if the Group is already associated with the User.</li></ol>
     */
    public void addGroup(long userId, long groupId) throws IllegalArgumentException;

    /**
     * Removes the {@link Group} with the submitted groupId from {@link User#groups} at the {@link User} with the submitted userId.
     *
     * @param userId  id if the User, must exist in the database.
     * @param groupId id of the Group, must exist in the database.
     * @throws IllegalArgumentException <ol><li>if no User with the submitted userId exists.</li>
     * <li>if no Group with the submitted groupId exists.</li>
     * <li>if the Group wasn't associated with the User at all.</li></ol>
     */
    public void removeGroup(long userId, long groupId) throws IllegalArgumentException;
    
    /**
     * Deletes the {@link User} with the submitted long.
     *
     * @param userId id of the User to be deleted, must exist in the database.
     * @throws IllegalArgumentException if no User with the submitted userId exists.
     */
    public void delete(long userId) throws IllegalArgumentException;

    /**
     * Searches for the {@link User} with the submitted name.
     * <p/>
     * Currently only used for testing purposes.
     *
     * @param username username of the User, must not be null or blank.
     * @return User - the found User or null.
     * @throws IllegalArgumentException if the submitted username is blank.
     * @throws NullPointerException     if the submitted username is null.
     */
    public Operator findByName(String username) throws IllegalArgumentException, NullPointerException;
    //XXX return User or throw?

}
