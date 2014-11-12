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
package eu.ggnet.dwoss.assembly.sample;

import eu.ggnet.dwoss.common.AbstractAccessCos;

import java.util.ArrayList;
import java.util.Arrays;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.api.AuthenticationException;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;

@ServiceProvider(service = Guardian.class)
public class NullAccessCos extends AbstractAccessCos implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        Operator login;
        login = (user.equals("test")) ? new Operator(user, 1, Arrays.asList(AtomicRight.values()))
                : new Operator(user, 1, new ArrayList<>());
        setRights(login);
    }
}
