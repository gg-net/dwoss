/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ee;

import java.util.List;
import java.util.Objects;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.core.system.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.eao.GroupEao;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

/**
 * Implementation of {@link GroupAgent}.
 *
 * @author mirko.schulze
 */
@Stateless
@LocalBean
public class GroupAgentBean extends AbstractAgentBean implements GroupAgent {

    @Inject
    @Rights
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @AutoLogger
    @Override
    public void create(String name) {
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( checkForNameDuplicate(name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        em.persist(new Persona(name));
    }

    @AutoLogger
    @Override
    public void updateName(long groupId, String name) {
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId);
        }
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        if ( checkForNameDuplicate(name) ) {
            throw new IllegalArgumentException("Submitted name " + name + " is already used.");
        }
        group.setName(name);
    }

    @AutoLogger
    @Override
    public void delete(long groupId) {
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId);
        }
        em.remove(group);
    }

    @AutoLogger
    @Override
    public void addRight(long groupId, AtomicRight right) {
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId);
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " is already granted to Group " + group.getName() + ".");
        }
        group.add(right);
    }

    @AutoLogger
    @Override
    public void removeRight(long groupId, AtomicRight right) {
        Persona group = em.find(Persona.class, groupId);
        if ( group == null ) {
            throw new IllegalArgumentException("No Group found with groupId = " + groupId);
        }
        Objects.requireNonNull(right, "Right must not be null.");
        if ( !group.getPersonaRights().contains(right) ) {
            throw new IllegalArgumentException("Submitted Right " + right + " was not granted to Group " + group.getName() + " at all.");
        }
        group.getPersonaRights().remove(right);
    }

    @AutoLogger
    @Override
    public Persona findByName(String name) {
        Objects.requireNonNull(name, "Submitted name is null.");
        if ( name.isBlank() ) {
            throw new IllegalArgumentException("Submitted name is blank.");
        }
        Persona group = new GroupEao(em).findByName(name);
        group.fetchEager();
        return group;
    }

    /**
     * Checks if a name is already used by another {@link Group}.
     * <p/>
     * Returns true if the name is already used.
     *
     * @param name name to check for duplicate.
     * @return boolean - true, if the submitted name is already used.
     */
    @AutoLogger
    private boolean checkForNameDuplicate(String name) {
        List<Persona> allGroups = findAll(Persona.class);
        return allGroups.stream().anyMatch(g -> g.getName().equals(name));
    }

}
