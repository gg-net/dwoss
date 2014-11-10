package eu.ggnet.dwoss.redtape.workflow;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.util.DocumentEquals;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.redtape.entity.util.DocumentEquals.Property.*;

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
        return newDocument;
    }
}
