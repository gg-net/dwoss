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

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.*;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RightsIT extends ArquillianProjectArchive {

    private static final Logger L = LoggerFactory.getLogger(RightsIT.class);

    @EJB
    private UserApi userApi;

    @EJB
    private GroupApi groupApi;

    private static final String USER_NAME = "Test User";

    private static final String GROUP_NAME = "Test Gruppe";

    private static final String SECOND_GROUP_NAME = "Test Gruppe Zwo";

    private static final char[] PASSWORD = {'q', 'w', 'e', 'r', 't'};

    private static final AtomicRight R = AtomicRight.CHANGE_TAX;

    @Test
    public void testRights() throws Exception {
        userApi.create(USER_NAME);

        assertThat(userApi.findAll().size()).isEqualTo(1);

        User user = userApi.findByName(USER_NAME);

        userApi.updatePassword(user.getId().get(), PASSWORD);

        user = null;

        user = userApi.authenticate(USER_NAME, PASSWORD);

        assertThat(user).isNotNull();

        groupApi.create(GROUP_NAME);
        groupApi.create(SECOND_GROUP_NAME);

        assertThat(groupApi.findAll().size()).isEqualTo(2);

        Group group = groupApi.findByName(GROUP_NAME);
        Group secondGroup = groupApi.findByName(SECOND_GROUP_NAME);

        groupApi.addRight(group.getId().get(), R);

        group = groupApi.findByName(GROUP_NAME);
        
        assertThat(group.getRights().size()).isEqualTo(1);

        userApi.addGroup(user.getId().get(), group.getId().get());
        userApi.addGroup(user.getId().get(), secondGroup.getId().get());

        user = userApi.findByName(USER_NAME);

        assertThat(user.getGroups().size()).isEqualTo(2);
        assertThat(user.getRights()).isEmpty();
        assertThat(user.getAllRights().size()).isEqualTo(1);

        groupApi.delete(group.getId().get());

        user = userApi.findByName(USER_NAME);

        assertThat(user.getGroups().size()).isEqualTo(1);
        assertThat(user.getAllRights()).isEmpty();
        assertThat(groupApi.findAll().size()).isEqualTo(1);

        groupApi.addRight(secondGroup.getId().get(), R);
        
        secondGroup = groupApi.findByName(SECOND_GROUP_NAME);

        assertThat(secondGroup.getRights().size()).isEqualTo(1);

        user = userApi.findByName(USER_NAME);

        assertThat(user.getRights()).isEmpty();
        assertThat(user.getAllRights().size()).isEqualTo(1);

        groupApi.delete(secondGroup.getId().get());
        userApi.addRight(user.getId().get(), R);

        user = userApi.findByName(USER_NAME);

        assertThat(user.getRights().size()).isEqualTo(1);
        assertThat(user.getAllRights().size()).isEqualTo(1);
        assertThat(user.getGroups()).isEmpty();
        assertThat(groupApi.findAll().size()).isZero();
    }

}
