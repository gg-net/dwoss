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
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.isA;

@RunWith(Arquillian.class)
public class UserApiIT extends ArquillianProjectArchive {

    @Inject
    @Rights
    private EntityManager em;

    @EJB
    private UserApi userApi;

    @Inject
    private UserTransaction utx;

    private final static String NAME = "Test User";

    private final static String UPDATED_NAME = "User Test";

    private static final String BLANK = "";

    private static final String GROUP_NAME = "Test Group";

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
        userApi.create(NAME);

        assertThat(userApi.findAll().size()).as("One existing User").isEqualTo(1);
        User found = userApi.findByName(NAME);
        assertThat(found).as("User exists").isNotNull();
    }

    @Test
    public void testCreateUsernameIsBlank() throws Exception {
        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.create(BLANK);
    }

    @Test
    public void testCreateUsernameIsNull() throws Exception {
        expectedException.expectCause(isA(NullPointerException.class));

        userApi.create(null);
    }

    @Test
    public void testUpdateUsername() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        userApi.updateUsername(user.getId(), UPDATED_NAME);

        User found = userApi.findByName(UPDATED_NAME);
        assertThat(found).as("User exists with new username").isNotNull();
    }

    @Test
    public void testUpdateUsernameUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.updateUsername(user.getId() + 1, UPDATED_NAME);
    }

    @Test
    public void testUpdateUsernameUsernameIsBlank() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.updateUsername(user.getId(), BLANK);
    }

    @Test
    public void testUpdateUsernameUsernameIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        userApi.updateUsername(user.getId(), null);
    }

    @Test
    public void testAddRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThat(user.getRights()).as("Existing User has no Rights").isEmpty();

        userApi.addRight(user.getId(), R);

        User found = userApi.findByName(NAME);
        assertThat(found.getRights()).as("Existing User has one Right").containsExactly(R);
    }

    @Test
    public void testAddRightUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.addRight(user.getId() + 1, R);
    }

    @Test
    public void testAddRightUserHasRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        user.add(R);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.addRight(user.getId(), R);
    }

    @Test
    public void testAddRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        userApi.addRight(user.getId(), null);
    }

    @Test
    public void testRemoveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.add(R);
        em.persist(user);
        utx.commit();

        assertThat(user.getRights()).as("Existing User has one Right").containsExactly(R);

        userApi.removeRight(user.getId(), R);

        User found = userApi.findByName(NAME);
        assertThat(found.getRights()).as("Existing User has no Rights").isEmpty();
    }

    @Test
    public void testRemoveRightUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.add(R);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.removeRight(user.getId() + 1, R);
    }

    @Test
    public void testRemoveRightUserDoesNotHaveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.removeRight(user.getId(), R);
    }

    @Test
    public void testRemoveRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        userApi.removeRight(user.getId(), null);
    }

    @Test
    public void testAddGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        assertThat(user.getPersonas()).as("Existing User has no Groups").isEmpty();

        userApi.addGroup(user.getId(), group.getId());

        User found = userApi.findByName(NAME);
        assertThat(found.getGroups()).as("Existing User has one Group").hasSize(1);
    }

    @Test
    public void testAddGroupUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.addGroup(user.getId() + 1, group.getId());
    }

    @Test
    public void testAddGroupGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.addGroup(user.getId(), group.getId() + 1);
    }

    @Test
    public void testAddGroupUserAlreadyHasGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        user.add(group);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.addGroup(user.getId(), group.getId());
    }

    @Test
    public void testRemoveGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        user.getPersonas().add(group);
        em.persist(user);
        em.persist(group);
        utx.commit();

        assertThat(user.getPersonas()).as("Existing User has one Group").hasSize(1);

        userApi.removeGroup(user.getId(), group.getId());

        User found = userApi.findByName(NAME);
        assertThat(found.getGroups()).as("Existing User has no Groups").isEmpty();
    }

    @Test
    public void testRemoveGroupUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.removeGroup(user.getId() + 1, group.getId());
    }

    @Test
    public void testRemoveGroupGroupIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.removeGroup(user.getId(), group.getId() + 1);
    }

    @Test
    public void testRemoveGroupUserDoesNotHaveGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.removeGroup(user.getId(), group.getId());
    }

    @Test
    public void testDelete() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThat(userApi.findAll().size()).as("One User exists").isEqualTo(1);

        userApi.delete(user.getId());

        assertThat(userApi.findAll().size()).as("No Users exist").isZero();
    }

    @Test
    public void testDeleteUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        userApi.delete(user.getId() + 1);
    }
}
