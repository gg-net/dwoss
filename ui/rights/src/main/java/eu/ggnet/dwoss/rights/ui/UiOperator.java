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
package eu.ggnet.dwoss.rights.ui;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.entity.Operator;

/**
 * This Entity represent a Operator in the Database with his rights.
 * <p>
 * @author Bastian Venz
 */
public class UiOperator implements Serializable {

    /**
     * Integer value for optimistic locking.
     */
    private int optLock;

    private final List<AtomicRight> rights = new ArrayList<>();

    private final List<UiPersona> personas = new ArrayList<>();

    private final ReadOnlyLongProperty idProperty;

    private final StringProperty usernameProperty = new SimpleStringProperty();

    private final IntegerProperty quickLoginKeyProperty = new SimpleIntegerProperty();

    private final StringProperty saltProperty = new SimpleStringProperty();

    private final StringProperty passwordProperty = new SimpleStringProperty();

    private ListProperty<UiPersona> personasProperty;

    private ListProperty<AtomicRight> rightsProperty;

    public Operator toOperator() {
        return new Operator(idProperty.get(), optLock, quickLoginKeyProperty.get(), usernameProperty.get(), saltProperty.get().getBytes(), passwordProperty.get().getBytes(), personas.stream().map(UiPersona::toPersona).collect(Collectors.toList()), rightsProperty.get());
    }

    public UiOperator(Operator operator) {
        this.optLock = operator.getOptLock();
        idProperty = new ReadOnlyLongWrapper(operator.getId());
        usernameProperty.set(operator.getUsername());
        Optional.ofNullable(operator.getPassword()).ifPresent(p -> passwordProperty.set(new String(p)));
        Optional.ofNullable(operator.getSalt()).ifPresent(s -> saltProperty.set(new String(s)));
        quickLoginKeyProperty.set(operator.getQuickLoginKey());
        this.rights.addAll(operator.getRights());
        operator.getPersonas().forEach(p -> this.personas.add(new UiPersona(p)));
    }

    public UiOperator() {
        this.idProperty = new ReadOnlyLongWrapper(0);
    }

    public int getOptLock() {
        return optLock;
    }

    public ReadOnlyLongProperty idProperty() {
        return idProperty;
    }

    public StringProperty usernameProperty() {
        return usernameProperty;
    }

    public IntegerProperty quickLoginKeyProperty() {
        return quickLoginKeyProperty;
    }

    public StringProperty saltProperty() {
        return saltProperty;
    }

    public StringProperty passwordProperty() {
        return passwordProperty;
    }

    public ListProperty<AtomicRight> rightsProperty() {
        if ( rightsProperty == null ) {
            rightsProperty = new SimpleListProperty<>(FXCollections.observableList(rights));
            rightsProperty.get().addListener((Change<? extends AtomicRight> change) -> {
                while (change.next()) {
                    if ( !change.wasAdded() ) continue;
                    for (AtomicRight addedRight : change.getAddedSubList()) {
                        for (UiPersona persona : personas) {
                            if ( persona.personaRightsProperty().get().contains(addedRight) ) {
                                change.getList().remove(addedRight);
                            }
                        }
                        if ( containsMoreThanOnce(rights, addedRight) ) {
                            change.getList().remove(addedRight);
                        }
                    }
                }
            });
        }
        return rightsProperty;
    }

    public ListProperty<UiPersona> personasProperty() {
        if ( personasProperty == null ) {
            personasProperty = new SimpleListProperty<>(FXCollections.observableList(personas));
            personasProperty.get().addListener((Change<? extends UiPersona> change) -> {
                while (change.next()) {
                    if ( change.wasAdded() ) {
                        for (UiPersona persona : change.getAddedSubList()) {
                            // Remove local duplicates
                            for (AtomicRight personaRight : persona.personaRightsProperty().get()) {
                                if ( getRights().contains(personaRight) ) {
                                    remove(personaRight);
                                }
                            }
                        }

                    }
                }
            });
        }
        return personasProperty;
    }

    /**
     * Defensive copy return of rights.
     * <p>
     * @return defensive copy return of rights
     */
    public List<AtomicRight> getRights() {
        return new ArrayList<>(rights);
    }

    /**
     * Returns all active rights, meaning the rights of this operator and all rights of assosiated personas.
     * <p>
     * @return all active rights.
     */
    public EnumSet<AtomicRight> getAllActiveRights() {
        EnumSet<AtomicRight> resultRights = EnumSet.noneOf(AtomicRight.class);
        resultRights.addAll(rights);
        for (UiPersona persona : personas) {
            resultRights.addAll(persona.personaRightsProperty().get());
        }
        return resultRights;
    }

    /**
     * Defensive copy return of persona.
     * <p>
     * @return defensive copy return of persona
     */
    public List<UiPersona> getPersonas() {
        return new ArrayList<>(personas);
    }

    /**
     * This method add a {@link AtomicRight} IF it not null or already in the List.
     * <p>
     * @param atomicRight
     */
    public void add(AtomicRight atomicRight) {
        if ( atomicRight != null && !rights.contains(atomicRight) ) {
            rights().add(atomicRight);
        }
    }

    public void remove(AtomicRight atomicRight) {
        rights().remove(atomicRight);
    }

    /**
     * This method add all {@link AtomicRight}'s IF it not null or already in the List.
     * <p>
     * @param right
     */
    public void addAllRight(Collection<AtomicRight> right) {
        for (AtomicRight atomicRight : right) {
            add(atomicRight);
        }
    }

    public void removeAllRight(Collection<AtomicRight> right) {
        rights().removeAll(right);
    }

    public void removeAllPersona(Collection<UiPersona> persona) {
        personas().removeAll(persona);
    }

    /**
     * This method add all {@link Persona}'s IF it not null or already in the List.
     * <p>
     * @param personas
     */
    public void addAllPersona(Collection<UiPersona> personas) {
        for (UiPersona p : personas) {
            add(p);
        }
    }

    private List<AtomicRight> rights() {
        if ( rightsProperty != null ) return rightsProperty.get();
        else return rights;
    }

    private List<UiPersona> personas() {
        if ( personasProperty != null ) return personasProperty.get();
        else return personas;
    }

    /**
     * This method add a {@link Persona} IF it not null or already in the List.
     * <p>
     * @param persona
     */
    private void add(UiPersona persona) {
        if ( persona != null && !personas.contains(persona) )
            personas().add(persona);
    }

    @Override
    public String toString() {
        return "Operator{" + "id=" + idProperty.get() + ", optLock=" + optLock + ", quickLoginKey=" + quickLoginKeyProperty.get() + ", rights=" + rights + ", username=" + usernameProperty.get() + ", salt=" + saltProperty.get() + ", password=" + passwordProperty.get() + '}';
    }

    private static <T> boolean containsMoreThanOnce(Collection<T> collection, T elem) {
        int count = 0;
        for (T t : collection) {
            if ( Objects.equals(t, elem) ) count++;
        }
        return count > 1;
    }

}
