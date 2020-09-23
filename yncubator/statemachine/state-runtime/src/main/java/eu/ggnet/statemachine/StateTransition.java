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
import java.util.Objects;


/**
 * Transition from one {@link State} to another, which changes an Instance.
 *
 * @author oliver.guenther
 */
public abstract class StateTransition<T> implements Serializable {

    private final String name;

    private final String description;

    private final String toolTip;

    public StateTransition(String name) {
        this(name, name, null);
    }

    public StateTransition(String name, String description, String toolTip) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.description = Objects.requireNonNull(description, "Description must not be null");
        this.toolTip = toolTip;
    }

    /**
     * The action to be applied on an instance to change its state.
     * The implementation should only change the instance without any sideffects (Persistence calls, Ui updates etc).
     * Otherwise future auto testing of the StateMachine will not be possible.
     *
     * @param instance the instance to manipulate
     */
    public abstract void apply(T instance);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getToolTip() {
        return toolTip;
    }
    
    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of name">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StateTransition<?> other = (StateTransition<?>)obj;
        if ( !Objects.equals(this.name, other.name) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return "{" + name + '}';
    }
}
