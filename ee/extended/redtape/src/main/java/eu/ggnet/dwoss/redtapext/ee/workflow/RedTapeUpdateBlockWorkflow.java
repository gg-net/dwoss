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

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;

/**
 * Workflow for updating a Document of Type Block.
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateBlockWorkflow extends RedTapeWorkflow {

    private final Document altered;

    public RedTapeUpdateBlockWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm, Document alteredDocument, String arranger, Mandator mandator) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.altered = alteredDocument;
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
        Document newDocument = refreshAndPrepare(altered, previous);
        equilibrateLogicTransaction(newDocument);
        newDocument.setHistory(new DocumentHistory(arranger, "Update durch " + this.getClass().getSimpleName()));
        redTapeEm.persist(newDocument);
        redTapeEm.flush(); // Writing new document an gennerating the id;
        L.debug("Returning {} with {}", newDocument, newDocument.getDossier());
        return newDocument;
    }
}
