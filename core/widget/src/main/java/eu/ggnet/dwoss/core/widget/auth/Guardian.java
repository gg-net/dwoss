/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.core.widget.auth;

import java.util.Set;

import javax.swing.Action;

import eu.ggnet.dwoss.rights.api.AtomicRight;

/**
 * Simple Authentication Interface.
 */
// Kandiate for Experimental
public interface Guardian {

    /**
     * Logout a possible authenticated user.
     */
    void logout();

    /**
     * Returns the usernames of all once logged in users.
     *
     * @return the usernames of all once logged in users.
     */
    Set<String> getOnceLoggedInUsernames();

    /**
     * Returns all usernames of the system, in a unmodifiable state.
     *
     * @return all usernames of the system.
     */
    Set<String> getAllUsernames();

    /**
     * Used to authenticate. Throws an exception if authentication is not successful.
     *
     * @param user the username
     * @param pass the password
     * @throws AuthenticationException is thrown if authentication is not successful
     */
    void login(String user, char[] pass) throws AuthenticationException;

    /**
     * Returns the active Rights or an empty Set.
     *
     * @return the active Rights or an empty Set.
     */
    Set<AtomicRight> getRights();

    /**
     * Returns the Username or an empty String if no one is authenticated yet.
     *
     * @return the Username or an empty String if no one is authenticated yet.
     */
    String getUsername();

    /**
     * The quick authentication mode, allows the change of the user be simply suppling his id.
     *
     * @param userId the userid.
     * @return true if userId was found and change was successful, otherwise false.
     */
    boolean quickAuthenticate(int userId);

    /**
     * Remove an accessDependet.
     *
     * @param instance the access dependent to remove.
     */
    void remove(Object instance);

    /**
     * Adds a UserChangeListener to the access.
     *
     * @param listener the listener
     */
    void addUserChangeListener(UserChangeListener listener);

    /**
     * Removes a UserChangeListener to the access.
     *
     * @param listener the listener
     */
    void removeUserChangeListener(UserChangeListener listener);

    /**
     * Add A {@link Accessable}.
     * The {@link Accessable} get called the method {@link Accessable#setEnabled(boolean)} with true
     * when the Rights are setted and the method {@link Accessable#getNeededRight()} return a {@link java.util.Set} of type {@link Authorisation}
     * containing.
     *
     * @param accessable which should be added in a intern List/Set.
     */
    public void add(Accessable accessable);

    /**
     * Add a object which has a setEnabled Method, like {@link Action#setEnabled(boolean)}.
     * It will wrap this in a AccessEnabler which then control it.
     *
     * @param enableAble    the object, that has an setEnable method
     * @param authorisation the Authorisation which is responsible for the enable
     */
    public void add(Object enableAble, AtomicRight authorisation);

    /**
     * This method remove a {@link Accessable} from an internal list.
     *
     * @param accessable the {@link Accessable} which should removed.
     */
    public void remove(Accessable accessable);

    /**
     * This method returns true if the current user have the given {@link Authorisation}.
     *
     * @param authorisation the given {@link Authorisation} which will be checked.
     * @return true if the current user have the given {@link Authorisation}.
     */
    public boolean hasRight(AtomicRight authorisation);
}
