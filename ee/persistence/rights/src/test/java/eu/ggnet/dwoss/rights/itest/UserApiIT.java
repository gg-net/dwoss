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

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.assist.gen.RightsDeleteUtils;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.rights.ee.op.PasswordUtil;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.*;

@RunWith(Arquillian.class)
public class UserApiIT extends ArquillianProjectArchive {

    private static final Logger L = LoggerFactory.getLogger(UserApiIT.class);

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

    private static final char[] PASSWORD = {'q', 'w', 'e', 'r', 't'};

    private static final char[] EMPTY_PASSWORD = {};

    private static final char[] WRONG_PASSWORD = {'z', 'u', 'i', 'o', 'p'};

    private static final byte[] SALT = RandomStringUtils.random(30).getBytes();

    private static final byte[] WRONG_SALT = RandomStringUtils.random(10).getBytes();

    private final static AtomicRight R = AtomicRight.CHANGE_TAX;

    @After
    public void tearDown() throws Exception {
        utx.begin();
        em.joinTransaction();
        RightsDeleteUtils.deleteAll(em);
        assertThat(RightsDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void testAuthenticate() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        User user = userApi.authenticate(NAME, PASSWORD);

        assertThat(user).as("User has been authenticated").isNotNull();
    }

    @Test
    public void testAuthenticateUsernameNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> {
            userApi.authenticate(UPDATED_NAME, PASSWORD);
        }).isInstanceOf(UserInfoException.class).hasMessageContaining("Benutzer " + UPDATED_NAME + " existiert nicht.");
    }

    @Test
    public void testAuthenticateUsernameIsBlank() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> {
            userApi.authenticate(BLANK, PASSWORD);
        }).isInstanceOf(UserInfoException.class).hasMessageContaining("Kein Nutzername angegeben.");
    }

    @Test
    public void testAuthenticatePasswordIsEmpty() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> userApi.authenticate(NAME, EMPTY_PASSWORD))
                .isInstanceOf(UserInfoException.class).hasMessageContaining("Kein Passwort angegeben.");
    }

    @Test
    public void testAuthenticateUsernameIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> userApi.authenticate(null, PASSWORD))
                .isInstanceOf(UserInfoException.class).hasMessageContaining("Kein Nutzername angegeben.");
    }

    @Test
    public void testAuthenticatePasswordIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> userApi.authenticate(NAME, null))
                .isInstanceOf(UserInfoException.class).hasMessageContaining("Kein Passwort angegeben.");
    }

    @Test
    public void testAuthenticatePasswordIsWrong() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> userApi.authenticate(NAME, WRONG_PASSWORD))
                .isInstanceOf(UserInfoException.class).hasMessageContaining("Authentifizierung nicht gelungen!");
    }

    @Test
    public void testAuthenticateSaltIsWrong() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator op = new Operator(NAME);
        op.setPassword(PasswordUtil.hashPassword(PASSWORD, SALT));
        op.setSalt(WRONG_SALT);
        em.persist(op);
        utx.commit();

        assertThatThrownBy(() -> userApi.authenticate(NAME, PASSWORD))
                .isInstanceOf(UserInfoException.class).hasMessageContaining("Authentifizierung nicht gelungen!");
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(userApi.findAll().size()).as("No existing Users").isZero();

        userApi.create(NAME);

        assertThat(userApi.findAll().size()).as("One existing User").isEqualTo(1);
        User found = userApi.findByName(NAME);
        assertThat(found).as("User exists").isNotNull();
    }

    @Test
    public void testCreateUsernameIsBlank() {
        assertThatThrownBy(() -> userApi.create(BLANK)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testCreateUsernameIsNull() throws Exception {
        assertThatThrownBy(() -> userApi.create(null)).hasCauseInstanceOf(NullPointerException.class);
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

        assertThatThrownBy(() -> userApi.updateUsername(user.getId() + 1, UPDATED_NAME)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdateUsernameUsernameIsBlank() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.updateUsername(user.getId(), BLANK)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdateUsernameUsernameIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.updateUsername(user.getId(), null)).hasCauseInstanceOf(NullPointerException.class);
    }

    @Test
    public void testUpdatePasswordUserIdNotFound() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.updatePassword(user.getId() + 1, PASSWORD)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdatePasswordPasswordIsEmpty() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.updatePassword(user.getId(), EMPTY_PASSWORD)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUpdatePasswordPasswordIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.updatePassword(user.getId(), null)).hasCauseInstanceOf(NullPointerException.class);
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

        assertThatThrownBy(() -> userApi.addRight(user.getId() + 1, R)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAddRightUserHasRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        user.add(R);
        utx.commit();

        assertThatThrownBy(() -> userApi.addRight(user.getId(), R)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAddRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.addRight(user.getId(), null)).hasCauseInstanceOf(NullPointerException.class);
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

        assertThatThrownBy(() -> userApi.removeRight(user.getId() + 1, R)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testRemoveRightUserDoesNotHaveRight() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.removeRight(user.getId(), R)).hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testRemoveRightRightIsNull() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator user = new Operator(NAME);
        em.persist(user);
        utx.commit();

        assertThatThrownBy(() -> userApi.removeRight(user.getId(), null)).hasCauseInstanceOf(NullPointerException.class);
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

        assertThatThrownBy(() -> userApi.addGroup(user.getId() + 1, group.getId())).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.addGroup(user.getId(), group.getId() + 1)).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.addGroup(user.getId(), group.getId())).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.removeGroup(user.getId() + 1, group.getId())).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.removeGroup(user.getId(), group.getId() + 1)).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.removeGroup(user.getId(), group.getId())).hasCauseInstanceOf(IllegalArgumentException.class);
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

        assertThatThrownBy(() -> userApi.delete(user.getId() + 1)).hasCauseInstanceOf(IllegalArgumentException.class);
    }
}
