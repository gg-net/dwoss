/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.statemachine;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a state of the state machine.
 * <p/>
 * A State is equal to another State, if all {@link StateCharacteristic}<code>s</code> are equal.
 * <p/>
 * A good practice is to have only one instance of a State.
 *
 * @author oliver.guenther
 */
public final class State<T> implements Serializable {

    public static enum Type {

        START, BETWEEN, END
    }

    private final String name;

    private final Type type;

    private final Set<StateCharacteristic<T>> characteristics;

    @SafeVarargs
    public State(Type type, String name, StateCharacteristic<T>... cs) {
        this(type, name, Arrays.asList(cs));
    }

    @SafeVarargs
    public State(String name, StateCharacteristic<T>... cs) {
        this(Type.BETWEEN, name, cs);
    }

    @SafeVarargs
    public State(String name, Collection<StateCharacteristic<T>>... css) {
        this(Type.BETWEEN, name, css);
    }

    @SafeVarargs
    public State(Type type, String name, Collection<StateCharacteristic<T>>... css) {
        characteristics = new HashSet<>();
        for (Collection<StateCharacteristic<T>> cs : css) characteristics.addAll(cs);
        this.name = name;
        this.type = type;
    }

    //<editor-fold desc="Getter" defaultstate="collapsed">
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Set<StateCharacteristic<T>> getCharacteristics() {
        return characteristics;
    }
    //</editor-fold>

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.characteristics);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final State other = (State)obj;
        if ( !Objects.equals(this.characteristics, other.characteristics) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "State{" + "name=" + name + ", type=" + type + ", characteristics=" + characteristics + '}';
    }
}




