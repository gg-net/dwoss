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
package eu.ggnet.dwoss.uniqueunit.api;

import java.util.Optional;

import javax.ejb.Remote;

/**
 * Finder for Units, based on the refurbishId.
 * 
 * @author oliver.guenther
 */
@Remote
public interface UnitFinder {
    
    /**
     * Returns a SimpleUnit, if something with the supplied refurbishId exists.
     * 
     * @param refurbishId
     * @return null or a simple unit, one exists.
     */
    SimpleUnit findBy(String refurbishId);
    
}
