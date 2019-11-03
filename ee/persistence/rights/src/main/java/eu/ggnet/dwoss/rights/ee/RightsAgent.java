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
package eu.ggnet.dwoss.rights.ee;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.ee.entity.Persona;
import eu.ggnet.dwoss.core.system.persistence.RemoteAgent;

/**
 * Agent for the Right Persistence Layer.
 *
 * @author Bastian Venz
 */
@Remote
public interface RightsAgent extends RemoteAgent {

    /**
     * Store, either persiting a new or merging an existing instance.
     * <p>
     * @param object
     * @return the stored instance
     */
    public Persona store(Persona object);

    /**
     * Store, either persiting a new or merging an existing instance.
     * <p>
     * @param object
     * @return the stored instance
     */
    public Operator store(Operator object);

    /**
     * Finds a Operator with the given username.
     * <p>
     * @param username is the username which the Operator should have.
     * @return a Operator with the given username.
     */
    public Operator findOperatorByUsername(String username);

}
