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
package eu.ggnet.dwoss.redtape.workflow;

import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.Document;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

import eu.ggnet.dwoss.rules.PaymentMethod;

/**
 * Workflow for updating a Document of Type Order.
 *
 * @author oliver.guenther
 */
public class RedTapeUpdateOrderWorkflow extends RedTapeWorkflow {

    final Document altered;

    public RedTapeUpdateOrderWorkflow(EntityManager redTapeEm, EntityManager uniqueUnitEm, EntityManager stockEm,
                                      Document alteredDocument, String arranger, Mandator mandator) {
        super(redTapeEm, uniqueUnitEm, stockEm, arranger, mandator);
        this.altered = alteredDocument;
    }

    /**
     * Update the Dossier if dispatch or PaymentMenthod changed.
     *
     * @param dossier       the dossier to be changed
     * @param dispatch      the possibly new dispatch
     * @param paymentMethod the possibly new paymentMethod
     * @return a String, either empty ("") if nothing has been changed, or containing the description of the change.
     */
    // TODO: Test
    private String optionalUpdateDossier(Dossier dossier, boolean dispatch, PaymentMethod paymentMethod) {
        String comment = "";
        if ( dossier.isDispatch() != dispatch ) {
            comment += (dispatch ? "Versandauftrag" : "Abholauftrag") + " zu "
                    + (dossier.isDispatch() ? "Versandauftrag" : "Abholauftrag") + " durch " + arranger + " geändert! ";
            dossier.setDispatch(dispatch);
            L.debug("Dossier changed dispatch: {}", dossier);
        }
        if ( dossier.getPaymentMethod() != paymentMethod ) {
            comment += paymentMethod + " zu " + dossier.getPaymentMethod() + " durch " + arranger + " geändert! ";
            dossier.setPaymentMethod(paymentMethod);
            L.debug("Dossier changed PaymentMethond: {}", dossier);
        }
        return comment;
    }

    /**
     * Returns true if the altered document has no significant changes in favour of the previous document
     *
     * @param altered  the altered document
     * @param previous the previous document
     * @return true if the altered document has no significant changes
     */
    private boolean noChanges(Document altered, Document previous) {
        if ( !altered.equalsContent(previous) ) return false;
        if ( altered.getDossier().isDispatch() != previous.getDossier().isDispatch() ) return false;
        if ( altered.getDossier().getPaymentMethod() != previous.getDossier().getPaymentMethod() ) return false;
        L.debug("The altered Document has no Changes to Document or Positions");
        return true;
    }

    /**
     * Executes the Workflow and returns a Document.
     *
     * @return a Document.
     */
    @Override
    public Document execute() {
        Document previous = new DocumentEao(redTapeEm).findById(altered.getId(), LockModeType.PESSIMISTIC_WRITE);
        if ( noChanges(altered, previous) ) return altered;
        L.info("Workflow on {} by {}", DocumentFormater.toSimpleLine(altered), arranger);
        validate(altered, previous);
        boolean isStillExactlyBriefed = altered.isStillExactlyBriefed(previous); // Must be asked here due to possible change on dossier
        String comment = optionalUpdateDossier(previous.getDossier(), altered.getDossier().isDispatch(), altered.getDossier().getPaymentMethod());
        Document newDocument = refreshAndPrepare(altered, previous);
        if ( altered.getConditions().contains(Document.Condition.CANCELED) ) {
            removeLogicTransaction(newDocument);
            comment += "Auftrag storniert.";
        } else {
            equilibrateLogicTransaction(newDocument);
            comment += "Auftrag geändert.";
        }
        if ( !isStillExactlyBriefed ) {
            L.debug("Remove Flag CUSTOMER_EXACTLY_BRIEFED." + newDocument);
            newDocument.remove(Document.Flag.CUSTOMER_EXACTLY_BRIEFED);
        }
        newDocument.setHistory(new DocumentHistory(arranger, comment));
        redTapeEm.persist(newDocument);
        L.debug("Returning {} with {}", newDocument, newDocument.getDossier());
        validateAfter(newDocument.getDossier());
        return newDocument;
    }
}
