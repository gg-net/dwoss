/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.redtape.workflow;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Document.Flag;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateInvoiceWorkflow extends RedTapeWorkflow {

    private final Document altered;

    public RedTapeUpdateInvoiceWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm,
                                        Document alteredDetachedDocument, String arranger, Mandator mandator) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.altered = alteredDetachedDocument;
    }

    @Override
    public Document execute() {
        Document previous = new DocumentEao(redTapeEm).findById(altered.getId(), LockModeType.PESSIMISTIC_WRITE);
        if ( altered.equalsContent(previous) ) return altered;
        L.info("Workflow on {} by {}", DocumentFormater.toSimpleLine(altered), arranger);
        validate(altered, previous);
        Document newDocument = refreshAndPrepare(altered, previous);
        if ( !newDocument.isClosed() ) {
            equilibrateLogicTransaction(newDocument);
            if ( altered.getType() != previous.getType() ) {
                // Allwasy set in the previos Order
                previous.setDirective(Document.Directive.NONE);
                generateIdentifier(newDocument);
            }
        }
        if ( !altered.isStillExactlyBriefed(previous) ) newDocument.remove(Flag.CUSTOMER_EXACTLY_BRIEFED);
        newDocument.setHistory(new DocumentHistory(arranger, "Update durch " + this.getClass().getSimpleName()));
        redTapeEm.persist(newDocument);
        validateAfter(newDocument.getDossier());
        return newDocument;
    }
}
