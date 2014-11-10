package eu.ggnet.dwoss.redtape.workflow;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;

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
        return newDocument;
    }
}
