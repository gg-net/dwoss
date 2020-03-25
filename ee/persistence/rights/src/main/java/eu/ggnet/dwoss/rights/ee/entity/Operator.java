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
package eu.ggnet.dwoss.rights.ee.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;
import eu.ggnet.dwoss.rights.api.AtomicRight;

import static javax.persistence.FetchType.EAGER;

/**
 * This Entity represent a Operator in the Database with his rights.
 * <p>
 * @author Bastian Venz
 */
@Entity
@NamedQuery(name = "Operator.byUsername", query = "Select i from Operator as i where i.username = ?1")
@NamedQuery(name = "Operator.byUsernameAndPasswordAndSalt", query = "Select i from Operator as i where i.username = ?1 AND i.password = ?2 AND i.salt = ?3")
@SuppressWarnings("PersistenceUnitPresent")
public class Operator extends BaseEntity implements Serializable, EagerAble {

    @Id
    @GeneratedValue
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
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
    private byte[] salt;

    /**
     * The byte[] which holds the Password.
     * Can be null if no Password is required.
     */
    private byte[] password;

    @ManyToMany(fetch = EAGER)
    @NotNull
    private List<Persona> personas = new ArrayList<>();

    public Operator(long id, int optLock, int quickLoginKey, String username, byte[] salt, byte[] password, List<Persona> personas, List<AtomicRight> rights) {
        this.id = id;
        this.optLock = optLock;
        this.quickLoginKey = quickLoginKey;
        this.username = username;
        this.salt = salt;
        this.password = password;
        Optional.ofNullable(personas).ifPresent(ps -> this.personas.addAll(ps));
        Optional.ofNullable(rights).ifPresent(r -> this.rights.addAll(r));
    }

    /**
     * This is a CopyConstructor which copys the Data from the DTO {@link eu.ggnet.dwoss.rights.api.Operator} to this class, EXCEPT for the Persona List!
     * <p>
     * @param dtoOperator
     */
    public Operator(eu.ggnet.dwoss.rights.api.Operator dtoOperator) {
        this.quickLoginKey = dtoOperator.quickLoginKey;
        this.rights = dtoOperator.rights();
        this.username = dtoOperator.username;
    }

    public Operator(String username) {
        this.username = username;
    }

    public Operator() {
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getOptLock() {
        return optLock;
    }

    @Override
    public long getId() {
        return id;
    }

    public byte[] getPassword() {
        return password;
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

    public void setPassword(byte[] password) {
        this.password = password;
    }
    //</editor-fold>

    public List<AtomicRight> getRights() {
        return rights;
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
            rights.add(atomicRight);
        }
    }

    /**
     * This method add a {@link Persona} IF it not null or already in the List.
     * <p>
     * @param persona
     */
    public void add(Persona persona) {
        if ( persona != null && !personas.contains(persona) )
            personas.add(persona);
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

    @Override
    public String toString() {
        return "Operator{" + "id=" + id + ", optLock=" + optLock + ", quickLoginKey=" + quickLoginKey + ", rights=" + rights + ", username=" + username + ", salt=" + salt + ", password=" + password + '}';
    }

}
