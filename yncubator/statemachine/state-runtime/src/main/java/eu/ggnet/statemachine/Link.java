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
 * Represents a Link between two States and a Transition.
 *
 * @author oliver.guenther
 */
public class Link<T> implements Serializable {

    private State<T> source;

    private StateTransition<T> transition;

    private State<T> destination;

    public Link() {
    }

    public Link(State<T> source, StateTransition<T> transition, State<T> destination) {
        this.source = source;
        this.transition = transition;
        this.destination = destination;
    }

    public State<T> getSource() {
        return source;
    }

    public StateTransition<T> getTransition() {
        return transition;
    }

    public State<T> getDestination() {
        return destination;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.source);
        hash = 89 * hash + Objects.hashCode(this.transition);
        hash = 89 * hash + Objects.hashCode(this.destination);
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
        final Link other = (Link)obj;
        if ( !Objects.equals(this.source, other.source) ) {
            return false;
        }
        if ( !Objects.equals(this.transition, other.transition) ) {
            return false;
        }
        if ( !Objects.equals(this.destination, other.destination) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Link{" + "source=" + source + ", transition=" + transition + ", destination=" + destination + '}';
    }
}
