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
package eu.ggnet.dwoss.redtape.ee.api;

import java.util.List;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.redtape.ee.entity.Dossier;

/**
 * Bridge Interface if someone wants to supply a legacy system. This is the part used in a remote clients.
 * It is assumed, that the legacy system and the dw use the same customer service, so ids are shared.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface LegacyRemoteBridge {

    /**
     * Should return a Human readable and useful name of the implementing system.
     * <p>
     * @return the name.
     */
    String remoteName();

    /**
     * Returns all Auftrags with no dossier id wrapped as Dossiers which are assigned to the supplied customer.
     * <p/>
     * @param customerId the customer
     * @param start      the start of the database result.
     * @param amount     the amount of the database result.
     * @return all Auftrags with no dossier id wrapped as Dossiers which are assigned to the supplied customer.
     */
    List<Dossier> findByCustomerId(long customerId, int start, int amount);

    /**
     * Returns a detailed HTML representation of existing instance like a dossier.
     * <p>
     * @param dossierIdentifier the identifier use in the legacy system
     * @return a detailed html.
     */
    String toDetailedHtmlDossier(String dossierIdentifier);

}
