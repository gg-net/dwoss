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
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

/**
 * Workflow for updating a Document of Type Order.
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateCapitalAssetReturnsWorkflow extends RedTapeWorkflow {

    final Document altered;

    public RedTapeUpdateCapitalAssetReturnsWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, Document alteredDocument, String arranger, Mandator mandator) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.altered = alteredDocument;
        // TODO: Validate Conditions.
    }

    /**
     * Executes the Workflow and returns a Document.
     *
     * @return a Document.
     */
    @Override
    public Document execute() {
        Document previous = new DocumentEao(redTapeEm).findById(altered.getId(), LockModeType.PESSIMISTIC_WRITE);
        if ( altered.equalsContent(previous) ) return altered;
        L.info("Workflow on {} by {}", DocumentFormater.toSimpleLine(altered), arranger);
        validate(altered, previous);
        String comment = "";
        Document newDocument = refreshAndPrepare(altered, previous);
        if ( altered.getConditions().contains(Document.Condition.CANCELED) ) {
            removeLogicTransaction(newDocument);
            comment += "Auftrag storniert.";
        } else {
            equilibrateLogicTransaction(newDocument);
            comment += "Auftrag geändert.";
        }
        if ( !altered.isStillExactlyBriefed(previous) ) newDocument.remove(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
        newDocument.setHistory(new DocumentHistory(arranger, comment));
        redTapeEm.persist(newDocument);
        L.debug("Returning {} with {}", newDocument, newDocument.getDossier());
        validateAfter(newDocument.getDossier());
        return newDocument;
    }
}
