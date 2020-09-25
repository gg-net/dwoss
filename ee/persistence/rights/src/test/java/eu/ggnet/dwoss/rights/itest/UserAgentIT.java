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

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.UserAgent;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.isA;

@RunWith(Arquillian.class)
public class UserAgentIT extends ArquillianProjectArchive {

    @Inject
    @Rights
    private EntityManager em;

    @EJB
    private UserAgent agent;

    @Inject
    private UserTransaction utx;

    private final static String NAME = "Test User";

    private final static String UPDATED_NAME = "User Test";

    private static final String BLANK = "";

    private static final String GROUP_NAME = "Test Group";

    private static final byte[] PASSWORD = {16, 1, 19, 19};

    private static final byte[] UPDATED_PASSWORD = {23, 15, 18, 3};

    private static final byte[] EMPTY_PASSWORD = {};

    private static final int QUICK_LOGIN_KEY = 0;

    private static final int UPDATED_QUICK_LOGIN_KEY = 1;

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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreate() throws Exception {
        agent.create(NAME);

        assertThat(agent.count(Operator.class)).as("One existing User").isEqualTo(1);
        Operator found = agent.findByName(NAME);
        assertThat(found).as("User exists").isNotNull();
    }

    @Test
    public void testCreateUsernameIsBlank() throws Exception {
        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.create(BLANK);
    }

    @Test
    public void testCreateUsernameIsNull() throws Exception {
        expectedException.expectCause(isA(NullPointerException.class));

        agent.create(null);
    }

    @Test
    public void testUpdateUsername() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        agent.updateUsername(user.getId(), UPDATED_NAME);

        Operator found = agent.findByName(UPDATED_NAME);
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

        agent.updateUsername(user.getId() + 1, UPDATED_NAME);
    }

    @Test
    public void testUpdateUsernameUsernameIsBlank() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.updateUsername(user.getId(), BLANK);
    }

    @Test
    public void testUpdateUsernameUsernameIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        agent.updateUsername(user.getId(), null);
    }

    @Test
    public void testUpdatePassword() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setPassword(PASSWORD);
        em.persist(user);
        utx.commit();

        agent.updatePassword(user.getId(), UPDATED_PASSWORD);

        Operator found = agent.findByName(NAME);
        assertThat(found.getPassword()).as("Existing User has new password").isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    public void testUpdatePasswordUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setPassword(PASSWORD);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.updatePassword(user.getId() + 1, UPDATED_PASSWORD);
    }

    @Test
    public void testUpdatePasswordPasswordIsEmpty() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setPassword(PASSWORD);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.updatePassword(user.getId(), EMPTY_PASSWORD);
    }

    @Test
    public void testUpdatePasswordPasswordIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setPassword(PASSWORD);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        agent.updatePassword(user.getId(), null);
    }

    @Test
    public void testUpdateQuickLoginKey() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setQuickLoginKey(QUICK_LOGIN_KEY);
        em.persist(user);
        utx.commit();

        agent.updateQuickLoginKey(user.getId(), UPDATED_QUICK_LOGIN_KEY);

        Operator found = agent.findByName(NAME);
        assertThat(found.getQuickLoginKey()).as("Existing User has new quickLoginKey").isEqualTo(UPDATED_QUICK_LOGIN_KEY);
    }

    @Test
    public void testUpdateQuickLoginKeyUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        user.setPassword(PASSWORD);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.updateQuickLoginKey(user.getId() + 1, UPDATED_QUICK_LOGIN_KEY);
    }

    @Test
    public void testAddRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThat(user.getRights()).as("Existing User has no Rights").isEmpty();

        agent.addRight(user.getId(), R);

        Operator found = agent.findByName(NAME);
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

        agent.addRight(user.getId() + 1, R);
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

        agent.addRight(user.getId(), R);
    }

    @Test
    public void testAddRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        agent.addRight(user.getId(), null);
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

        agent.removeRight(user.getId(), R);

        Operator found = agent.findByName(NAME);
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

        agent.removeRight(user.getId() + 1, R);
    }

    @Test
    public void testRemoveRightUserDoesNotHaveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.removeRight(user.getId(), R);
    }

    @Test
    public void testRemoveRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(NullPointerException.class));

        agent.removeRight(user.getId(), null);
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

        agent.addGroup(user.getId(), group.getId());

        Operator found = agent.findByName(NAME);
        assertThat(found.getPersonas()).as("Existing User has one Group").hasSize(1);
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

        agent.addGroup(user.getId() + 1, group.getId());
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

        agent.addGroup(user.getId(), group.getId() + 1);
    }

    @Test
    public void testAddGroupUserHasGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        user.add(group);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.addGroup(user.getId(), group.getId());
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

        agent.removeGroup(user.getId(), group.getId());

        Operator found = agent.findByName(NAME);
        assertThat(found.getPersonas()).as("Existing User has no Groups").isEmpty();
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

        agent.removeGroup(user.getId() + 1, group.getId());
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

        agent.removeGroup(user.getId(), group.getId() + 1);
    }

    @Test
    public void testRemoveGroupUserHasGroup() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        Persona group = new Persona(GROUP_NAME);
        em.persist(user);
        em.persist(group);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.removeGroup(user.getId(), group.getId());
    }

    @Test
    public void testDelete() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThat(agent.count(Operator.class)).as("One User exists").isEqualTo(1);

        agent.delete(user.getId());

        assertThat(agent.count(Operator.class)).as("No Users exist").isZero();
    }

    @Test
    public void testDeleteUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        expectedException.expectCause(isA(IllegalArgumentException.class));

        agent.delete(user.getId() + 1);
    }
}
