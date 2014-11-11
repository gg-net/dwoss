/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.rights.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.util.persistence.EagerAble;
import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import lombok.*;

/**
 * This Class represent a Persona
 * <p>
 * @author Bastian Venz
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Persona extends IdentifiableEntity implements Serializable, Comparable<Persona>, EagerAble {

    @Id
    @GeneratedValue
    @Getter
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    private int optLock;

    @NotNull
    @Getter
    @Setter
    private String name;

    @ElementCollection
    @NotNull
    private List<AtomicRight> personaRights = new ArrayList<>();

    @Transient
    private transient ReadOnlyLongProperty idProperty;

    @Transient
    private transient StringProperty nameProperty;

    @Transient
    private transient ObjectProperty<ObservableList<AtomicRight>> personaRightsProperty;

    public Persona(String name) {
        this.name = name;
    }

    public ReadOnlyLongProperty idProperty() {
        if ( idProperty == null ) {
            idProperty = new ReadOnlyLongWrapper(id);
        }
        return idProperty;
    }

    public StringProperty nameProperty() {
        if ( nameProperty == null ) {
            nameProperty = new SimpleStringProperty(name);
            nameProperty.addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                    name = newValue;
                }
            });
        }
        return nameProperty;
    }

    public ObjectProperty<ObservableList<AtomicRight>> personaRightsProperty() {
        if ( personaRightsProperty == null ) {
            personaRightsProperty = new SimpleObjectProperty<>(FXCollections.observableList(personaRights));
        }
        return personaRightsProperty;
    }

    /**
     * Defensive copy return of persona rights.
     * <p>
     * @return defensive copy return of persona rights
     */
    public List<AtomicRight> getPersonaRights() {
        return new ArrayList<>(personaRights);
    }

    public void add(AtomicRight atomicRight) {
        if ( atomicRight != null && !personaRights.contains(atomicRight) )
            personaRights.add(atomicRight);
    }

    public void addAll(Collection<AtomicRight> newRights) {
        for (AtomicRight atomicRight : newRights) {
            if ( atomicRight != null && !personaRights.contains(atomicRight) )
                personaRights.add(atomicRight);
        }
    }

    public void remove(AtomicRight atomicRight) {
        personaRights.remove(atomicRight);
    }

    public void removeAll(Collection<AtomicRight> rights) {
        personaRights.removeAll(rights);
    }

    @Override
    public int compareTo(Persona o) {
        if ( o == null ) return -1;
        if ( o.equals(this) ) return 0;
        if ( !o.getName().equals(this.getName()) ) return this.getName().compareToIgnoreCase(o.getName());
        if ( o.getId() > this.getId() ) return 1;
        if ( o.getId() < this.getId() ) return -1;
        return o.personaRights.hashCode() - this.personaRights.hashCode();
    }

    @Override
    public void fetchEager() {
        personaRights.size();
    }

}
