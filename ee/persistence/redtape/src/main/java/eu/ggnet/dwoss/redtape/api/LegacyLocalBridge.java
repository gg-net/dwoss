/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.api;

import java.util.List;

import javax.ejb.Local;

/**
 * Bridge Interface if someone wants to supply a legacy system. This is the part used in the local system.
 * It is assumed, that the legacy system and the dw use the same customer service, so ids are shared.
 * <p/>
 * @author oliver.guenther
 */
@Local
public interface LegacyLocalBridge {

    /**
     * Should return a Human readable and useful name of the implementing system.
     * <p>
     * @return the name.
     */
    String localName();

    /**
     * A full Text Search for Units.
     * <p>
     * @param search the full text search string.
     * @return a unit representation.
     */
    List<LegacyUnit> findUnit(String search);

    /**
     * Returns true if the supplied identifier does not collied with some unit identifier in the legacy system.
     * For Sopo this was the SopoNr.
     * This also implies, if this method returns false, the {@link LegacyRemoteBridge#toDetailedHtmlUnit(java.lang.String) } will return a useful result.
     * <p>
     * @param identifier the identifier to verify
     * @return true if not used.
     */
    boolean isUnitIdentifierAvailable(String identifier);

    /**
     * Returns a detailed HTLM representation of an existing instance like a unit.
     * <p>
     * @param unitIdentifier the unit identifier
     * @return detailed html
     */
    String toDetailedHtmlUnit(String unitIdentifier);
}
