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
package eu.ggnet.dwoss.assembly.remote.provides;

import java.util.stream.Collectors;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.op.Authentication;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.dl.LocalSingleton;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

/**
 * Implementation of an IAuthenticator using the GG-Net Imap Server
 */
@ServiceProvider(service = Guardian.class)
public class LookupAuthenticationGuardian extends AbstractGuardian implements Guardian, LocalSingleton {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        if ( getAllUsernames().isEmpty() ) {
            setAllUsersnames(Dl.remote().lookup(RightsAgent.class).findAll(Operator.class).stream().map(Operator::getUsername).collect(Collectors.toSet()));
        }

        try {
            setRights(Dl.remote().lookup(Authentication.class).login(user, pass));
        } catch (UserInfoException ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }
}
