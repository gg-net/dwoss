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
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.isA;

@RunWith(Arquillian.class)
public class GroupApiIT extends ArquillianProjectArchive {

    @Inject
    @Rights
    private EntityManager em;

    @EJB
    private GroupApi groupApi;

    @Inject
    private UserTransaction utx;

    private final static String NAME = "Test Group";

    private final static String UPDATED_NAME = "Group Test";

    private static final String BLANK = "";

    private final static AtomicRight R = AtomicRight.CHANGE_TAX;

    @After
    public void tearDown() throws Exception {
        utx.begin();
        em.joinTransaction();
        Utils.clearH2Db(em);
        utx.commit();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreate() throws Exception {
        groupApi.create(NAME);

        assertThat(groupApi.findAll().size()).as("One existing Group").isEqualTo(1);

        Group found = groupApi.findByName(NAME);
        assertThat(found).as("Group exists").isNotNull();
    }

    @Test
    public void testCreateNameIsBlank() throws Exception {
        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.create(BLANK);
    }

    @Test
    public void testCreateNameIsNull() throws Exception {
        expectedException.expectCause(isA(NullPointerException.class));

        groupApi.create(null);

    }

    @Test
    public void testCreateNameIsDuplicate() throws Exception {
        groupApi.create(NAME);

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.create(NAME);
    }

    @Test
    public void testUpdateName() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        groupApi.updateName(group.getId(), UPDATED_NAME);

        Group found = groupApi.findByName(UPDATED_NAME);
        assertThat(found).as("Group exists with new name").isNotNull();
    }

    @Test
    public void testUpdateNameGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.updateName(group.getId() + 1, UPDATED_NAME);
    }

    @Test
    public void testUpdateNameNameIsBlank() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.updateName(group.getId(), BLANK);
    }

    @Test
    public void testUpdateNameNameIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        groupApi.updateName(group.getId(), null);
    }

    @Test
    public void testUpdateNameNameIsAlreadyUsedByAnotherGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        Persona anotherGroup = new Persona(UPDATED_NAME);
        em.persist(anotherGroup);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.updateName(group.getId(), UPDATED_NAME);
    }

    @Test
    public void testAddRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        groupApi.addRight(group.getId(), R);

        Group found = groupApi.findByName(NAME);
        assertThat(found).as("Group exists").isNotNull();
        assertThat(found.getRights()).as("Existing Group has one right").containsExactly(R);
    }

    @Test
    public void testAddRightGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.addRight(group.getId() + 1, R);
    }

    @Test
    public void testAddRightGroupHasRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        group.add(R);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.addRight(group.getId(), R);
    }

    @Test
    public void testAddRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        groupApi.addRight(group.getId(), null);
    }

    @Test
    public void testRemoveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        group.add(R);
        em.persist(group);
        utx.commit();

        groupApi.removeRight(group.getId(), R);

        Group found = groupApi.findByName(NAME);
        assertThat(found).as("Group exists").isNotNull();
        assertThat(found.getRights()).as("Existing Group has no rights").isEmpty();
    }

    @Test
    public void testRemoveRightGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.removeRight(group.getId() + 1, R);
    }

    @Test
    public void testRemoveRightGroupDoesNotHaveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.removeRight(group.getId(), R);
    }

    @Test
    public void testRemoveRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        groupApi.removeRight(group.getId(), null);
    }

    @Test
    public void testDelete() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        assertThat(groupApi.findAll().size()).as("One Group exists").isEqualTo(1);

        groupApi.delete(group.getId());

        assertThat(groupApi.findAll().size()).as("No Groups exist").isZero();
    }

    @Test
    public void testDeleteGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Persona group = new Persona(NAME);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        groupApi.delete(group.getId() + 1);
    }
}
