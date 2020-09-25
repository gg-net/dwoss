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
import eu.ggnet.dwoss.rights.ee.entity.Persona;

/**
 * Agent for the persistence layer of the rights module for {@link Group} entitites.
 * <p/>
 * Contains methods to create, update or delete a Group and to add or remove an {@link AtomicRight} to/from a Group.
 *
 * @author mirko.schulze
 */
@Remote
public interface GroupAgent extends RemoteAgent {

    /**
     * Creates a new {@link Group}.
     *
     * @param name name for the new Group, must not be null or blank.
     * @throws IllegalArgumentException <ol><li>if the submitted name is blank.</li>
     * <li>if the submitted name is already used by another Group.</li></ol>
     * @throws NullPointerException     if the submitted name is null.
     */
    public void create(String name) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted name as new {@link Group#name} at the {@link Group} with the submitted groupId.
     *
     * @param groupId id of the Group, must exist in the database.
     * @param name    new name for the Group, must not be null or blank.
     * @throws IllegalArgumentException <ol><li>if no Group with the submitted groupId exists.</li>
     * <li>if the submitted name is blank.</li>
     * <li>if the submitted name is already used by another Group.</li></ol>
     * @throws NullPointerException     if the submitted name is null.
     */
    public void updateName(long groupId, String name) throws IllegalArgumentException, NullPointerException;

    /**
     * Deletes the {@link Group} with the submitted groupId.
     *
     * @param groupId id of the Group to be deleted, must exist in the database.
     * @throws IllegalArgumentException if no Group with the submitted groupId exists.
     */
    public void delete(long groupId) throws IllegalArgumentException;

    /**
     * Adds the submitted {@link AtomicRight} to the {@link Group} with the submitted groupId.
     *
     * @param groupId id of the Group, must exist in the database.
     * @param right   Right to be added, must not be null.
     * @throws IllegalArgumentException <ol><li>if no Group with the submitted groupId exists.</li>
     * <li>if the submitted Right is already granted to the Group.</li></ol>
     * @throws NullPointerException     if the submitted Right is null.
     */
    public void addRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Removes the submitted {@link AtomicRight} to the {@link Group} with the submitted groupId.
     *
     * @param groupId id of the Group, must exist in the database.
     * @param right   Right to be removed, must not be null.
     * @throws IllegalArgumentException <ol><li>if no Group with the submitted groupId exists.</li>
     * <li>if the submitted Right wasn't granted to the Group at all.</li></ol>
     * @throws NullPointerException     if the submitted Right is null.
     */
    public void removeRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Searches for the {@link Group} with the submitted name.
     * <p/>
     * Currently only used for testing purposes.
     *
     * @param name name of the Group, must not be null or blank.
     * @return Group - the found Group or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    public Persona findByName(String name) throws IllegalArgumentException, NullPointerException;
    //XXX return Persona or throw

}
