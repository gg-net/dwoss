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
package eu.ggnet.dwoss.redtapext.ee;

import jakarta.ejb.Remote;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Flag;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;

/**
 * Supporter for Documents, mainly for mail or printing.
 *
 * @author oliver.guenther
 */
@Remote
public interface DocumentSupporter {

    /**
     * This method sends a document to the e-Mail address of the customer.
     *
     * @param document This is the Document that will be send.
     * @param jtype
     * @throws UserInfoException if the sending of the Mail is not successful.
     * @throws RuntimeException  if problems exist in the JasperExporter
     */
    void mail(Document document, DocumentViewType jtype) throws UserInfoException, RuntimeException;

    /**
     * Creates a JasperPrint for the Document.
     *
     * @param document the document
     * @param viewType
     * @return a JasperPrint
     * @throws UserInfoException if document cannot be rendered, e.g. multitax document.
     */
    JasperPrint render(Document document, DocumentViewType viewType) throws UserInfoException;

    /**
     * Sets the Flags {@link Flag#CUSTOMER_BRIEFED} and {@link Flag#CUSTOMER_EXACTLY_BRIEFED} at the document.
     * Also appends this change at the DocumentHistory.
     *
     * @param detached the document
     * @param arranger
     * @return the updated document
     */
    Dossier briefed(Document detached, String arranger);

    FileJacket toXls(String identifier);

}
