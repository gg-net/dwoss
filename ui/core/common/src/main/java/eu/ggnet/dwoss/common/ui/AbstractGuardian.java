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
package eu.ggnet.dwoss.common.ui;

import java.util.*;

import javax.swing.Action;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.saft.api.Authorisation;
import eu.ggnet.saft.experimental.auth.*;

/**
 * An Implementation which handles the AccessDependent and Rights Storage, but without an actual Authentication.
 * Extends this class and in {@link AccessCos#login(java.lang.String, java.lang.String) } call setRights.
 * <p/>
 * @author oliver.guenther
 */
public abstract class AbstractGuardian implements Guardian {

    private final List<UserChangeListener> userChangeListeners = new ArrayList<>();

    private final Set<Accessable> accessables = new HashSet<>();

    private final Set<AtomicRight> rights = new HashSet<>();

    private Operator operator;

    private final Map<Integer, Operator> quickRights = new HashMap<>();

    private final Set<String> allUsers = new HashSet<>();

    @Override
    public Set<String> getOnceLoggedInUsernames() {
        Set<String> loggedInUsernames = new HashSet<>();
        for (Operator op : quickRights.values()) {
            loggedInUsernames.add(op.username);
        }
        return loggedInUsernames;
    }

    @Override
    public void logout() {
        this.operator = null;
        for (Accessable accessable : accessables) {
            accessable.setEnabled(false);
        }
        for (UserChangeListener listener : userChangeListeners) {
            listener.loggedOut();
        }
    }

    @Override
    public boolean quickAuthenticate(int userId) {
        if ( !quickRights.containsKey(userId) ) return false;
        setRights(quickRights.get(userId));
        return true;
    }

    @Override
    public String getUsername() {
        return (operator == null ? "" : operator.username);
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
     */
    protected void setRights(Operator dto) {
        operator = dto;
        quickRights.put(dto.quickLoginKey, dto);
        for (Accessable accessable : accessables) {
            accessable.setEnabled(false);
        }
        rights.clear();
        rights.addAll(dto.rights());
        for (Accessable accessable : accessables) {
            for (AtomicRight atomicRight : dto.rights()) {
                if ( accessable.getNeededRight().equals(atomicRight) )
                    accessable.setEnabled(true);
            }
        }
        if ( !StringUtils.isBlank(dto.username) ) {
            for (UserChangeListener listener : userChangeListeners) {
                listener.loggedIn(dto.username);
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
    public void add(Object enableAble, Authorisation atomicRight) {
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
    public boolean hasRight(Authorisation atomicRight) {
        return rights.contains(atomicRight);
    }

    @Override
    public Set<Authorisation> getRights() {
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
