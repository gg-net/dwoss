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
package eu.ggnet.dwoss.redtapext.ee.workflow;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.common.api.values.PositionType;

import static eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals.Property.*;

/**
 * Workflow for updating a Document of Type Complaint.
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateComplaintWorkflow extends RedTapeWorkflow {

    final Document altered;

    public RedTapeUpdateComplaintWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, Document alteredDocument, String arranger, Mandator mandator) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.altered = alteredDocument;
    }

    private void validateChangeAllowed(Document altered, Document previous) {
        if ( !new DocumentEquals()
                .ignore(ID, ACTIVE, HISTORY, PREDECESSOR, DIRECTIVE, FLAGS, CONDITIONS)
                .igonrePositionOrder()
                .ignorePositions(PositionType.COMMENT)
                .equals(altered, previous) )
            throw new RuntimeException("A Change was tried on a Document, which is not allowed in ComplaintWorkflow:\n"
                    + "  altered=" + altered + "\n  previous=" + previous);
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
        if ( altered.getType() == previous.getType() ) validateChangeAllowed(altered, previous);
        Document newDocument = refreshAndPrepare(altered, previous);
        newDocument.setHistory(new DocumentHistory(arranger, "Update durch " + this.getClass().getSimpleName()));
        redTapeEm.persist(newDocument);
        redTapeEm.flush(); // Writing new document an gennerating the id;
        L.debug("Returning {} with {}", newDocument, newDocument.getDossier());
        return newDocument;
    }
}
