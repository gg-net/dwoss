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

import java.util.Collection;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

/**
 * This Class represent a Persona
 * <p>
 * @author Bastian Venz
 */
public class UiPersona {

    /**
     * Integer value for optimistic locking.
     */
    private final int optLock;

    private final ReadOnlyLongProperty idProperty;

    private final StringProperty nameProperty = new SimpleStringProperty();

    private final ObjectProperty<ObservableList<AtomicRight>> personaRightsProperty;

    public Persona toPersona() {
        return new Persona(idProperty.get(), optLock, nameProperty.get(), personaRightsProperty.get());
    }

    public UiPersona(Persona persona) {
        this.idProperty = new ReadOnlyLongWrapper(persona.getId());
        this.optLock = persona.getOptLock();
        this.nameProperty.set(persona.getName());
        this.personaRightsProperty = new SimpleObjectProperty<>(FXCollections.observableList(persona.getPersonaRights()));
    }

    public UiPersona() {
        this.optLock = 0;
        this.idProperty = new ReadOnlyLongWrapper(0);
        this.personaRightsProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    }

    public int getOptLock() {
        return optLock;
    }

    public ReadOnlyLongProperty idProperty() {
        return idProperty;
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public ObjectProperty<ObservableList<AtomicRight>> personaRightsProperty() {
        return personaRightsProperty;
    }

    public void addAll(Collection<AtomicRight> newRights) {
        for (AtomicRight atomicRight : newRights) {
            if ( atomicRight != null && !personaRightsProperty.get().contains(atomicRight) )
                personaRightsProperty.get().add(atomicRight);
        }
    }

    @Override
    public final int hashCode() {
        if ( idProperty.get() == 0 ) return super.hashCode(); // Not persisted.
        return this.getClass().hashCode() * 7 + (int)(idProperty.get() ^ (idProperty.get() >>> 32));
    }

    public boolean equals(Object two) { // Used in Remove of unused personas
        UiPersona one = this;
        if ( two == null ) return false;
        if ( one.getClass() != two.getClass() ) return false;
        final UiPersona other = (UiPersona)two;
        if ( one.idProperty.get() == 0 && other.idProperty.get() == 0 ) return one == other; // Not persisted use object identity
        return one.idProperty.get() == other.idProperty.get();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
