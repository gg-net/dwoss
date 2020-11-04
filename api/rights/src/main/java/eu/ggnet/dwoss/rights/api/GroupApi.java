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

/**
 * Interface for the persistence layer of the Rights module for {@link Persona} entitites.
 * <p/>
 * Contains methods to create, update or delete Personas and to add or remove an {@link AtomicRight} to/from a Persona.
 *
 * @author oliver.guenther
 */
@Remote
public interface GroupApi {

    /**
     * Creates a new {@link Persona} in the database and returns a {@link Group} representation of that Persona.
     *
     * @param name name for the new Persona, must not be null or blank.
     * @return Group - representation the new Persona.
     * @throws IllegalArgumentException <ol><li>if the submitted name is blank.</li>
     * <li>if the submitted name is already used by another Persona.</li></ol>
     * @throws NullPointerException     if the submitted name is null.
     */
    Group create(String name) throws IllegalArgumentException, NullPointerException;

    /**
     * Sets the submitted name as new name at the {@link Persona} with the submitted groupId and returns a {@link Group} representation of that Persona.
     *
     * @param groupId id of the Persona, must exist in the database.
     * @param name    new name for the Persona, must not be null or blank.
     * @return Group - representation of the Persona that was modified.
     * @throws IllegalArgumentException <ol><li>if no Persona with the submitted groupId exists.</li>
     * <li>if the submitted name is blank.</li>
     * <li>if the submitted name is already used by another Persona.</li></ol>
     * @throws NullPointerException     if the submitted name is null.
     */
    Group updateName(long groupId, String name) throws IllegalArgumentException, NullPointerException;

    /**
     * Adds the submitted {@link AtomicRight} to the {@link Persona} with the submitted groupId and returns a {@link Group} representation of that Persona.
     *
     * @param groupId id of the Persona, must exist in the database.
     * @param right   AtomicRight to be added, must not be null.
     * @return Group - representation of the Persona that was modified.
     * @throws IllegalArgumentException <ol><li>if no Persona with the submitted groupId exists.</li>
     * <li>if the submitted AtomicRight is already granted to the Persona.</li></ol>
     * @throws NullPointerException     if the submitted AtomicRight is null.
     */
    Group addRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Removes the submitted {@link AtomicRight} from the {@link Persona} with the submitted groupId and returns a {@link Group} representation of that Persona.
     *
     * @param groupId id of the Persona, must exist in the database.
     * @param right   AtomicRight to be removed, must not be null.
     * @return Group - representation of the Persona that was modified.
     * @throws IllegalArgumentException <ol><li>if no Persona with the submitted groupId exists.</li>
     * <li>if the submitted AtomicRight wasn't granted to the Persona at all.</li></ol>
     * @throws NullPointerException     if the submitted AtomicRight is null.
     */
    Group removeRight(long groupId, AtomicRight right) throws IllegalArgumentException, NullPointerException;

    /**
     * Deletes the {@link Persona} with the submitted groupId from the database.
     *
     * @param groupId id of the Persona to be deleted, must exist in the database.
     * @throws IllegalArgumentException if no Persona with the submitted groupId exists.
     */
    void delete(long groupId) throws IllegalArgumentException;

    /**
     * Searches for the {@link Persona} with the submitted groupId and returns a {@link Group} representation of that Persona.
     *
     * @param groupId id of the Persona, must exist in the database.
     * @return Group - representation of the found Persona or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    Group findById(long groupId) throws IllegalArgumentException;

    /**
     * Searches for the {@link Persona} with the submitted name and returns a {@link Group} representation of that Persona.
     *
     * @param name name of the Persona, must not be null or blank.
     * @return Group - representation of the found Persona or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    Group findByName(String name) throws IllegalArgumentException, NullPointerException;

    /**
     * Searches for all {@link Persona}<code>s</code> and returns a List of {@link Group} with representations of those Personas.
     *
     * @return List&lt;Group&gt; - representations of the found Personas or null.
     * @throws IllegalArgumentException if the submitted name is blank.
     * @throws NullPointerException     if the submitted name is null.
     */
    List<Group> findAll();

}
