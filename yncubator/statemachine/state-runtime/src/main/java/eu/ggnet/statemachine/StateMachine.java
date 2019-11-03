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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A StateMachine. The Usage is best shown on an example, a Box: <ul> <li>Two States: open, close</li> <li>Two Transitions: closing and opening</li> <li>The
 * Graph: open &rarr; closing &rarr; closed; closed &rarr; opening &rarr; open</li> </ul> The Box: (For simplicity, a Box is already a
 * {@link StateCharacteristic}
 * <pre><code> public class Box implements StateCharacteristic&lt;Box&gt; {
 *
 *   private boolean closed;
 *
 *   public boolean isClosed() { return closed; }
 *
 *   public void setClosed(boolean closed) { this.closed = closed; }
 *
 *   public int hashCode() { return 89 * 3 + (closed ? 1 : 0); }
 *
 *   public boolean equals(Object obj) {
 *     if (obj == null) { return false; }
 *     if (getClass() != obj.getClass()) { return false; }
 *     final BoxStateCharacteristics other = (BoxStateCharacteristics) obj;
 *     if (this.closed != other.closed) { return false; }
 *     return true;
 *   }
 *
 * }</code></pre> The {@link StateCharacteristicFactory}:
 * <pre><code> public class BoxStateCharacteristicFactory implements StateCharacteristicFactory<Box> {
 *
 *   public StateCharacteristic<Box> characterize(Box t) { return t; }
 *
 * }</code></pre> Create the States:
 * <pre><code> public class BoxStates {
 *
 *   public final static State OPEN = new State("OPEN", new Box(false));
 *   public final static State CLOSED = new State("CLOSED", new Box(true));
 *
 * }</code></pre> Create the Transitions:
 * <pre><code> public class BoxTransitions {
 *   public final static StateTransition<Box> OPENING = new StateTransition<Box>("OPENING") {
 *     public void apply(Box instance) { instance.setClosed(false); }
 *   };
 *
 *   public final static StateTransition<Box> CLOSING = new StateTransition<Box>("OPENING") {
 *     public void apply(Box instance) { instance.setClosed(true); }
 *   };
 *
 * }</code></pre> Create the Machine and use it:
 * <pre><code> public class Main {
 *
 *   public static void main(String ... args) {
 *     // Init the Machine
 *     StateMachine<Box> m = new StateMachine<>(new BoxStateCharacteristicFactory());
 *     m.add(BoxStates.OPEN, BoxTransitions.CLOSING, BoxStates.CLOSED);
 *     m.add(BoxStates.CLOSED, BoxTransitions.OPENING, BoxStates.OPEN);
 *     // Create a Box (default closed=false)
 *     Box b = new Box();
 *     // Use the Machine to open or close it.
 *     m.stateChange(b, BoxStates.CLOSING);
 *     System.out.println("Box should be closed: closed=" + b.isClosed());
 *     m.stateChange(b, BoxStates.OPENING);
 *     System.out.println("Box should be open: closed=" + b.isClosed());
 *     m.show(): // Display the Graph
 *   }
 *
 * }</code></pre>
 * Notes from the author: This implementation of a StateMachine is far from being an all round solution.
 * But it serves it's cause, by being used in RedTape.
 *
 * @author oliver.guenther
 */
public class StateMachine<T> {

    private final static Logger L = LoggerFactory.getLogger(StateMachine.class);

    private boolean debug;

    private StateCharacteristicFactory<T> factory;

    private StateFormater<T> formater;

    private Set<Link<T>> links = new HashSet<>();

    private Set<State<T>> states = new HashSet<>();

    private Map<StateCharacteristic<T>, State> allCharacteristics = new HashMap<>();

    public StateMachine(StateCharacteristicFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * Creates a new empty state machine using the same factory.
     * <p/>
     * @param stateMachine the initial state machine.
     */
    public StateMachine(StateMachine<T> stateMachine) {
        this.factory = stateMachine.factory;
    }

    public boolean isDebug() {
        return debug;
    }

    public StateFormater<T> getFormater() {
        return formater;
    }

    public void setFormater(StateFormater<T> formater) {
        this.formater = formater;
    }

    /**
     * Enables some System.out Debug. TODO: Change to some Logger
     *
     * @param debug if true, enables debug.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Changes the State of an instance.
     * Validates if, the instance is in a definite state and if the transition can be applied.
     *
     * @param instance   the instance to change the state
     * @param transition the transition to be used.
     * @throws IllegalArgumentException if the instance is not in a definite state or the transition cannot be applied.
     *
     */
    public void stateChange(T instance, StateTransition<T> transition) throws IllegalArgumentException {
        L.debug("stateChange for {} with {}", instance, transition);

        if ( debug ) {
            System.out.println("Transfer: " + instance);
            System.out.println(" - " + getState(instance));
            System.out.println(" - " + transition);
        }

        if ( getState(instance) == null ) throw new IllegalArgumentException(instance + " has no State defined");
        if ( !getPossibleTransitions(instance).contains(transition) ) throw new IllegalArgumentException(transition + " is not possible on State" + instance);
        transition.apply(instance);
        if ( debug ) {
            System.out.println(" - " + getState(instance));
        }
    }

    /**
     * Adds a new Link consisting of two states and a transition to the machine.
     *
     * @param source      the source state.
     * @param transition  the transition between the two states.
     * @param destination the destination state.
     */
    public void add(State<T> source, StateTransition<T> transition, State<T> destination) {
        add(new Link<>(source, transition, destination));
    }

    /**
     * Adds a new Link consisting of two states and a transition to the machine.
     * <p/>
     * @param link the link to add
     */
    public void add(Link<T> link) {
        if ( links.contains(link) ) throw new IllegalArgumentException("Adding an existing Link, not allowed: " + link);
        validAdd(link.getSource());
        validAdd(link.getDestination());
        links.add(link);
    }

    private void validAdd(State<T> state) {
        if ( states.contains(state) ) return;
        for (StateCharacteristic<T> c : state.getCharacteristics()) {
            if ( allCharacteristics.keySet().contains(c) ) {
                throw new IllegalArgumentException("The added state has an overlapping characteristic. Added " + state.getName()
                        + ", existing " + allCharacteristics.get(c).getName() + ", overlapping " + c);
            }
        }
        states.add(state);
        for (StateCharacteristic<T> c : state.getCharacteristics()) {
            allCharacteristics.put(c, state);
        }
    }

    /**
     * Return all States of the machine.
     *
     * @return all States.
     */
    public Set<State<T>> getAllStates() {
        return new HashSet<>(states);
    }

    /**
     * Returns the State, which is identifies the instance or null if no State matches.
     *
     * @param instance the instance
     * @return the State, which is identifies the instance or null if no State matches.
     */
    public State<T> getState(T instance) {
        StateCharacteristic<T> characteristic = factory.characterize(instance);
        if ( characteristic == null ) return null;
        for (State<T> state : getAllStates()) {
            if ( state.getCharacteristics().contains(characteristic) ) {
                return state;
            }
        }
        return null;
    }

    /**
     * Return all Links.
     *
     * @return all Links.
     */
    public Set<Link<T>> getLinks() {
        return new HashSet<>(links);
    }

    /**
     * Returns a possible empty List of transactions, which can be applied to the instance.
     *
     * @param instance the instance.
     * @return a possible empty List of transactions, which can be applied to the instance.
     */
    public List<StateTransition<T>> getPossibleTransitions(T instance) {
        StateCharacteristic<T> characteristic = factory.characterize(instance);
        List<StateTransition<T>> sts = new ArrayList<>();
        for (Link<T> link : links) {
            if ( link.getSource().getCharacteristics().contains(characteristic) ) {
                sts.add(link.getTransition());
            }
        }
        return sts;
    }
}
