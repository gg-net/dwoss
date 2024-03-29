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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;
import eu.ggnet.dwoss.rights.api.*;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * This Entity represent a Operator in the Database with his rights.
 * <p>
 * @author Bastian Venz
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Operator extends BaseEntity implements Serializable, EagerAble {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    private short optLock = 0;

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

    public Operator(long id, short optLock, int quickLoginKey, String username, byte[] salt, byte[] password, List<Persona> personas, List<AtomicRight> rights) {
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

    public List<Persona> getPersonas() {
        return personas;
//        return new ArrayList<>(personas);
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
     * Creates and returns a {@link User} representation of this {@link Operator}.
     *
     * @return User - representation this Operator.
     */
    public User toApiUser() {
        List<Group> groups = new ArrayList<>();
        this.personas.forEach(p -> groups.add(p.toApiGroup()));
        return new User.Builder()
                .setId(Optional.of(this.id))
                .setOptLock(this.optLock)
                .setUsername(this.username)
                .addAllRights(this.rights)
                .addAllGroups(groups)
                .build();
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
