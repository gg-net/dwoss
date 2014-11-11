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
package eu.ggnet.dwoss.assembly.remote.cos;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.api.AuthenticationException;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.common.AbstractAccessCos;
import eu.ggnet.dwoss.rights.op.Authentication;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * Implementation of an IAuthenticator using the GG-Net Imap Server
 */
@ServiceProvider(service = Guardian.class)
public class ImapAccessCos extends AbstractAccessCos implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        try {
            setRights(lookup(Authentication.class).login(user, pass));
        } catch (UserInfoException ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }
}
