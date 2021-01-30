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
package eu.ggnet.dwoss.core.widget;

import java.util.*;

import javax.swing.Action;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.auth.*;
import eu.ggnet.dwoss.rights.api.*;

/**
 * An Implementation which handles the AccessDependent and Rights Storage, but without an actual Authentication.
 * Extends this class and in {@link AccessCos#login(java.lang.String, java.lang.String) } call setRights.
 * <p>
 * @author oliver.guenther
 */
// Todo: I think all the hard dependecies to the rights api could be moved to the final implementation in the LookupAuthenctionGuardian.
public abstract class AbstractGuardian implements Guardian {

    private final Logger L = LoggerFactory.getLogger(AbstractGuardian.class);

    private final List<UserChangeListener> userChangeListeners = new ArrayList<>();

    private final Set<Accessable> accessables = new HashSet<>();

    private final Set<AtomicRight> rights = new HashSet<>();

    private User user;

    private final Map<Integer, User> quickUser = new HashMap<>();

    private final Set<String> allUsers = new HashSet<>();

    public AbstractGuardian() {
        L.debug("New instance of {}", this);
    }

    @Override
    public Set<String> getOnceLoggedInUsernames() {
        Set<String> loggedInUsernames = new HashSet<>();
        for (User op : quickUser.values()) {
            loggedInUsernames.add(op.getUsername());
        }
        return loggedInUsernames;
    }

    @Override
    public void logout() {
        this.user = null;
        for (Accessable accessable : accessables) {
            accessable.setEnabled(false);
        }
        for (UserChangeListener listener : userChangeListeners) {
            listener.loggedOut();
        }
    }

    @Override
    public boolean quickAuthenticate(int quickLogin) {
        if ( !quickUser.containsKey(quickLogin) ) return false;
        setUserAndQuickLogin(quickUser.get(quickLogin), quickLogin);
        return true;
    }

    @Override
    public String getUsername() {
        return (user == null ? "" : user.getUsername());
    }

    @Override
    public Set<String> getAllUsernames() {
        return Collections.unmodifiableSet(allUsers);
    }

    /**
     * Sets all users.
     *
     * @param allUsers all users.
     */
    protected void setAllUsersnames(Collection<String> allUsers) {
        Optional.ofNullable(allUsers).ifPresent(c -> {
            this.allUsers.clear();
            this.allUsers.addAll(c);
        });
    }

    /**
     * This method set the Current {@link AtomicRight}'s to the given one and Enables/Disables all {@link Accessable} components.
     * <p>
     * @param dto is a {@link Operator} that will be setted.
     * @deprecated Use {@link AbstractGuardian#setUserAndQuickLogin(eu.ggnet.dwoss.rights.api.User, java.lang.Integer) }.
     */
    @Deprecated
    protected void setRights(Operator dto) {
        Objects.requireNonNull(dto, "operator must not be null");
        setUserAndQuickLogin(new User.Builder().setUsername(dto.username).addAllRights(dto.rights()).build(), dto.quickLoginKey);
    }

    /**
     * This method uses all information of the user and the optional quickLogin to internal use it in the Guardian.
     * <p>
     *
     * @param user       the user, must not be null
     * @param quickLogin the quicklogin, may be null.
     * @throws NullPointerException if user is null.
     */
    protected void setUserAndQuickLogin(User user, Integer quickLogin) throws NullPointerException {
        L.debug("setUserAndQuickLogin(user={},quickLogin={}) accessables.size = {}, userChangeListeners.size = {} ", user, quickLogin);
        this.user = Objects.requireNonNull(user, "user must not be null");
        if ( quickLogin != null && quickLogin.intValue() > 0 ) quickUser.put(quickLogin, user);
        for (Accessable accessable : accessables) {
            accessable.setEnabled(false);
        }
        rights.clear();
        rights.addAll(user.getRights());
        for (Accessable accessable : accessables) {
            for (AtomicRight atomicRight : user.getRights()) {
                if ( accessable.getNeededRight().equals(atomicRight) )
                    accessable.setEnabled(true);
            }
        }
        if ( !StringUtils.isBlank(user.getUsername()) ) {
            for (UserChangeListener listener : userChangeListeners) {
                listener.loggedIn(user.getUsername());
            }
        }
    }

    /**
     * Add A {@link Accessable}.
     * The {@link Accessable} get called the method {@link Accessable#setEnabled(boolean)} with true
     * when the Rights are setted and the method {@link Accessable#getNeededRights()} returns a collection which has the {@link AtomicRight}
     * containing.
     * <p>
     * @param accessable which should be added in a intern List/Set.
     */
    @Override
    public void add(Accessable accessable) {
        accessables.add(accessable);
        accessable.setEnabled(false);
        for (AtomicRight atomicRight : rights) {
            if ( accessable.getNeededRight() == atomicRight ) accessable.setEnabled(true);
        }

    }

    /**
     * * Add a object which has a setEnabled Method, like {@link Action#setEnabled(boolean)}.
     * It will wrap this in a AccessEnabler which then control it.
     * It will call the {@link Guardian#add(Accessable) } method with a {@link AccessEnabler} which wraps the Object.
     * <p>
     * @param enableAble  is the Object which has a setEnabled Method
     * @param atomicRight is the {@link AtomicRight} which is needed.
     */
    @Override
    public void add(Object enableAble, AtomicRight atomicRight) {
        if ( atomicRight == null ) throw new NullPointerException("Supplied AtomicRight is null");
        AccessEnabler ae = new AccessEnabler(enableAble, atomicRight);
        add(ae);
    }

    /**
     * This method remove a {@link Accessable} from an internal list.
     * <p>
     * @param accessable the {@link Accessable} which should removed.
     */
    @Override
    public void remove(Accessable accessable) {
        accessables.remove(accessable);
    }

    @Override
    public void remove(Object instance) {
        // AE uses the hashCode and Equals of the Instance.
        AccessEnabler ae = new AccessEnabler(instance);

        for (Accessable accessable : new HashSet<>(accessables)) {
            if ( accessable.equals(ae) ) accessables.remove(accessable);
        }
    }

    /**
     * Tis method returns {@link Boolean#TRUE} if the current user have the given {@link AtomicRight}.
     * <p>
     * @param atomicRight the given {@link AtomicRight} wich will be checked.
     * @return {@link Boolean#TRUE} if the current user have the given {@link AtomicRight}.
     */
    @Override
    public boolean hasRight(AtomicRight atomicRight) {
        return rights.contains(atomicRight);
    }

    @Override
    public Set<AtomicRight> getRights() {
        return new HashSet<>(rights);
    }

    @Override
    public void addUserChangeListener(UserChangeListener listener) {
        userChangeListeners.add(listener);
    }

    @Override
    public void removeUserChangeListener(UserChangeListener listener) {
        userChangeListeners.remove(listener);
    }

}
