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
package eu.ggnet.dwoss.rights.api;

import java.io.Serializable;
import java.util.*;

/**
 * This is a Data Transfer Object for {@link Operator}.
 * <p>
 * @author Bastian Venz
 * <p>
 */
public class Operator implements Serializable {

    public final String username;

    public final int quickLoginKey;

    private final List<AtomicRight> rights;

    public Operator(String username, int quickLoginKey, List<AtomicRight> rights) {
        this.username = Objects.requireNonNull(username,"username must not be null");
        if (username.trim().isEmpty()) throw new IllegalArgumentException("username must not be blank");
        this.quickLoginKey = Objects.requireNonNull(quickLoginKey,"quickLoginKey must not be null");
        this.rights = new ArrayList<>();
        if (rights != null) {
            this.rights.addAll(rights);
        }
    }

    public List<AtomicRight> rights() {
        return Collections.unmodifiableList(rights);
    }

    @Override
    public String toString() {
        return "Operator{" + "username=" + username + ", quickLoginKey=" + quickLoginKey + ", rights=" + rights + '}';
    }   

}
