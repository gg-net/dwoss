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
package eu.ggnet.dwoss.rights.api;

//import eu.ggnet.dwoss.rights.ee.assist.Rights;
//import eu.ggnet.dwoss.core.system.autolog.AutoLogger;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.rights.ee.entity.QPersona.persona;

/**
 *
 * @author mirko.schulze
 */
@Stateless
public class GroupApiBean implements GroupApi {
    
    @Inject
    @Rights
    private EntityManager em;

    @Override
    public Group findById(long id) throws IllegalArgumentException {
        Persona group = new JPAQuery<Persona>(em).from(persona).where(persona.id.eq(id)).fetchOne();
        if(group == null) throw new IllegalArgumentException("No Group found with id " + id + ".");
        return new Group.Builder()
                .setId(group.getId())
                .setName(group.getName())
                .setOptLock(group.getOptLock())
                .addAllRights(group.getPersonaRights())
                .build();
    }

    @Override
    public Group findByName(String name) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(name, "Submitted name is null,");
        Persona group = new JPAQuery<Persona>(em).from(persona).where(persona.name.eq(name)).fetchOne();
        if(group == null) throw new IllegalArgumentException("No Group found with name " + name + ".");
        return new Group.Builder()
                .setId(group.getId())
                .setName(group.getName())
                .setOptLock(group.getOptLock())
                .addAllRights(group.getPersonaRights())
                .build();
    }

    @Override
    public List<Group> findAll() {
        List<Persona> personas = new JPAQuery<Persona>(em).from(persona).fetch();
        List<Group> groups = new ArrayList<>();
        personas.forEach(p -> {
            groups.add(new Group.Builder()
                    .setId(p.getId())
                    .setName(p.getName())
                    .setOptLock(p.getOptLock())
                    .addAllRights(p.getPersonaRights()).build());
        });
        return groups;
    }


}
