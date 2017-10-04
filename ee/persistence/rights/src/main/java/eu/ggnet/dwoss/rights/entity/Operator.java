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
package eu.ggnet.dwoss.rights.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.util.persistence.EagerAble;
import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

import lombok.*;

import static javax.persistence.FetchType.EAGER;

/**
 * This Entity represent a Operator in the Database with his rights.
 * <p>
 * @author Bastian Venz
 */
@Entity
@NoArgsConstructor
@ToString(exclude = "personas")
@NamedQueries({
    @NamedQuery(name = "Operator.byUsername", query = "Select i from Operator as i where i.username = ?1")
    ,
    @NamedQuery(name = "Operator.byUsernameAndPasswordAndSalt", query = "Select i from Operator as i where i.username = ?1 AND i.password = ?2 AND i.salt = ?3")})
public class Operator extends IdentifiableEntity implements Serializable, EagerAble {

    @Id
    @GeneratedValue
    @Getter
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    @Getter
    @Setter
    private int optLock;

    private int quickLoginKey;

    @ElementCollection
    @NotNull
    private List<AtomicRight> rights = new ArrayList<>();

    @NotNull
    private String username;

    /**
     * The byte[] which holds the salt of the Password.
     * Can be null if no Password is required.
     */
    @Getter
    @Setter
    private byte[] salt;

    /**
     * The byte[] which holds the Password.
     * Can be null if no Password is required.
     */
    @Getter
    private byte[] password;

    @ManyToMany(fetch = EAGER)
    @NotNull
    private List<Persona> personas = new ArrayList<>();

    @Transient
    private transient ReadOnlyLongProperty idProperty;

    @Transient
    private transient StringProperty usernameProperty;

    @Transient
    private transient IntegerProperty quickLoginKeyProperty;

    @Transient
    private transient ListProperty<AtomicRight> rightsProperty;

    @Transient
    private transient StringProperty saltProperty;

    @Transient
    private transient StringProperty passwordProperty;

    @Transient
    private transient ListProperty<Persona> personasProperty;

    /**
     * This is a CopyConstructor which copys the Data from the DTO {@link eu.ggnet.dwoss.rights.api.Operator} to this class, EXCEPT for the Persona List!
     * <p>
     * @param dtoOperator
     */
    public Operator(eu.ggnet.dwoss.rights.api.Operator dtoOperator) {
        this.quickLoginKey = dtoOperator.getQuickLoginKey();
        this.rights = dtoOperator.getRights();
        this.username = dtoOperator.getUsername();
    }

    public Operator(String username) {
        this.username = username;
    }

    public int getQuickLoginKey() {
        return quickLoginKey;
    }

    public void setQuickLoginKey(int quickLoginKey) {
        this.quickLoginKey = quickLoginKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ReadOnlyLongProperty idProperty() {
        if ( idProperty == null ) {
            idProperty = new ReadOnlyLongWrapper(id);
        }
        return idProperty;
    }

    public StringProperty usernameProperty() {
        if ( usernameProperty == null ) {
            usernameProperty = new SimpleStringProperty(username);
            usernameProperty.addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                    username = newValue;
                }
            });
        }
        return usernameProperty;
    }

    public IntegerProperty quickLoginKeyProperty() {
        if ( quickLoginKeyProperty == null ) {
            quickLoginKeyProperty = new SimpleIntegerProperty(quickLoginKey);
            quickLoginKeyProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    quickLoginKey = newValue.intValue();
                }
            });
        }
        return quickLoginKeyProperty;
    }

    public ListProperty<AtomicRight> rightsProperty() {
        if ( rightsProperty == null ) {
            rightsProperty = new SimpleListProperty<>(FXCollections.observableList(rights));
            rightsProperty.get().addListener(new ListChangeListener<AtomicRight>() {

                @Override
                public void onChanged(Change<? extends AtomicRight> change) {
                    while (change.next()) {
                        if ( !change.wasAdded() ) continue;
                        for (AtomicRight addedRight : change.getAddedSubList()) {
                            for (Persona persona : personas) {
                                if ( persona.getPersonaRights().contains(addedRight) ) {
                                    change.getList().remove(addedRight);
                                }
                            }
                            if ( containsMoreThanOnce(rights, addedRight) ) {
                                change.getList().remove(addedRight);
                            }
                        }
                    }
                }
            });
        }
        return rightsProperty;
    }

    private static <T> boolean containsMoreThanOnce(Collection<T> collection, T elem) {
        int count = 0;
        for (T t : collection) {
            if ( Objects.equals(t, elem) ) count++;
        }
        return count > 1;
    }

    public ListProperty<Persona> personasProperty() {
        if ( personasProperty == null ) {
            personasProperty = new SimpleListProperty<>(FXCollections.observableList(personas));
            personasProperty.get().addListener(new ListChangeListener<Persona>() {

                @Override
                public void onChanged(Change<? extends Persona> change) {
                    while (change.next()) {
                        if ( change.wasAdded() ) {
                            for (Persona persona : change.getAddedSubList()) {
                                // Remove local duplicates
                                for (AtomicRight personaRight : persona.getPersonaRights()) {
                                    if ( getRights().contains(personaRight) ) {
                                        remove(personaRight);
                                    }
                                }
                            }

                        }
                    }
                }
            });
        }
        return personasProperty;
    }

    public StringProperty saltProperty() {
        if ( saltProperty == null ) {
            String saltString = (salt != null) ? new String(salt) : "";
            saltProperty = new SimpleStringProperty(saltString);
            saltProperty.addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                    salt = newValue.getBytes();
                }
            });
        }
        return saltProperty;
    }

    public StringProperty passwordProperty() {
        if ( passwordProperty == null ) {
            String pw = (password != null) ? new String(password) : "";
            passwordProperty = new SimpleStringProperty(pw);
            passwordProperty.addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
                    password = newValue.getBytes();
                }
            });
        }
        return passwordProperty;
    }

    public void setPassword(byte[] password) {
        if ( passwordProperty != null ) passwordProperty.set((password != null) ? new String(password) : "");
        else this.password = password;
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
        for (Persona persona : personas) {
            resultRights.addAll(persona.getPersonaRights());
        }
        return resultRights;
    }

    /**
     * Defensive copy return of persona.
     * <p>
     * @return defensive copy return of persona
     */
    public List<Persona> getPersonas() {
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

    /**
     * This method add a {@link Persona} IF it not null or already in the List.
     * <p>
     * @param persona
     */
    public void add(Persona persona) {
        if ( persona != null && !personas.contains(persona) )
            personas().add(persona);
    }

    public void remove(Persona persona) {
        personas().remove(persona);
    }

    public void removeAllPersona(Collection<Persona> persona) {
        personas().removeAll(persona);
    }

    /**
     * This method add all {@link Persona}'s IF it not null or already in the List.
     * <p>
     * @param persona
     */
    public void addAllPersona(Collection<Persona> persona) {
        for (Persona persona1 : persona) {
            add(persona1);
        }
    }

    /**
     * Create a {@link eu.ggnet.dwoss.rights.api.Operator} object from this instance.
     * <p>
     * @return
     */
    public eu.ggnet.dwoss.rights.api.Operator toDto() {
        return new eu.ggnet.dwoss.rights.api.Operator(username, quickLoginKey, new ArrayList<>(getAllActiveRights()));
    }

    /**
     * This method will be called bevor persisting and will remove all duplicated rights in the rights list and remove all rights that are already in the
     * Personas.
     */
    @PrePersist
    @PreUpdate
    public void preStrored() {
        for (Persona persona : personas) {
            rights.removeAll(persona.getPersonaRights());
        }
    }

    @Override
    public void fetchEager() {
        rights.size();
        for (Persona persona : personas) {
            persona.fetchEager();
        }
    }

    private List<AtomicRight> rights() {
        if ( rightsProperty != null ) return rightsProperty.get();
        else return rights;
    }

    private List<Persona> personas() {
        if ( personasProperty != null ) return personasProperty.get();
        else return personas;
    }

}
