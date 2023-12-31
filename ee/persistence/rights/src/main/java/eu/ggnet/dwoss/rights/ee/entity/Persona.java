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

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Group;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * This Class represent a Persona
 * <p>
 * @author Bastian Venz
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Persona extends BaseEntity implements Serializable, Comparable<Persona>, EagerAble {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @Version
    private short optLock = 0;

    @NotNull
    private String name;

    @ElementCollection
    @NotNull
    private List<AtomicRight> personaRights;

    public Persona() {
        personaRights = new ArrayList<>();
    }

    /**
     * Copy constructor for usage in ui. See the old ui usage pattern.
     *
     * @param id            the id
     * @param optLock       the old optLock
     * @param name          the name
     * @param personaRights collection of the persona rights.
     */
    public Persona(long id, short optLock, String name, List<AtomicRight> personaRights) {
        this();
        this.id = id;
        this.optLock = optLock;
        this.name = Objects.requireNonNull(name, "Name must not be null");
        Optional.ofNullable(personaRights).ifPresent(prs -> this.personaRights.addAll(prs));
    }

    public Persona(String name) {
        this();
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getOptLock() {
        return optLock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AtomicRight> getPersonaRights() {
//        return new ArrayList<>(personaRights);
        return personaRights;
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
    
    /**
     * Creates and returns a {@link Group} representation of this {@link Persona}.
     *
     * @return Group - representation this Persona.
     */
    public Group toApiGroup(){
        return new Group.Builder()
                .setId(Optional.of(this.id))
                .setOptLock(this.optLock)
                .setName(this.name)
                .addAllRights(this.personaRights)
                .build();
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
