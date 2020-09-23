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
package eu.ggnet.dwoss.rights.itest;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.GroupAgent;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author mirko.schulze
 */
@RunWith(Arquillian.class)
public class GroupAgentIT extends ArquillianProjectArchive {

    private final static Logger L = LoggerFactory.getLogger(GroupAgentIT.class);

    @Inject
    @Rights
    private EntityManager em;

    @EJB
    private GroupAgent agent;

    @Inject
    private UserTransaction utx;

    private final static String NAME = "Test Group";

    private final static String UPDATED_NAME = "Group Test";

    private final static AtomicRight R = AtomicRight.CHANGE_TAX;

    @After
    public void tearDown() throws Exception {
        utx.begin();
        em.joinTransaction();

        if ( em == null ) {
            return;
        }
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        em.createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'")
                .getResultList()
                .stream().forEachOrdered((table) -> {
                    em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
                });

        em.createNativeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'")
                .getResultList()
                .stream().forEachOrdered((sequence) -> {
                    em.createNativeQuery("ALTER SEQUENCE " + sequence + " RESTART WITH 1").executeUpdate();
                });
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        utx.commit();
    }

    @Test
    public void testCreate() throws Exception {
        agent.create(NAME);

        assertThat(agent.count(Persona.class)).as("One existing Group").isEqualTo(1);
        Persona found = agent.findByName(NAME);
        assertThat(found).as("Group exists").isNotNull();
    }

    @Test
    public void testUpdateName() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        agent.updateName(group.getId(), UPDATED_NAME);

        Persona found = agent.findByName(UPDATED_NAME);
        assertThat(found).as("Group exists with new name").isNotNull();
    }

    @Test
    public void testAddRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        agent.addRight(group.getId(), R);

        Persona found = agent.findByName(NAME);
        assertThat(found).as("Group exists").isNotNull();
        assertThat(found.getPersonaRights()).as("Existing Group has one right").containsExactly(R);
    }

    @Test
    public void testRemoveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        L.info("1: size of Groups rights = {}", group.getPersonaRights().size());
        group.add(R);
        L.info("2: size of Groups rights = {}", group.getPersonaRights().size());
        em.persist(group);
        utx.commit();

        agent.removeRight(group.getId(), R);

        Persona found = agent.findByName(NAME);
        L.info("3: size of Groups rights = {}", found.getPersonaRights().size());
        assertThat(found).as("Group exists").isNotNull();
        assertThat(found.getPersonaRights()).as("Existing Group has no rights").isEmpty();
    }

    @Test
    public void testDelete() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        assertThat(agent.count(Persona.class)).as("One Group exists").isEqualTo(1);

        agent.delete(group.getId());

        assertThat(agent.count(Persona.class)).as("No Groups exist").isZero();
    }
}
