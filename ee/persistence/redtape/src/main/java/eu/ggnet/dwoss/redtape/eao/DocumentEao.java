package eu.ggnet.dwoss.redtape.eao;

import java.util.*;

import javax.persistence.*;

import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.entity.Document.Directive;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

/**
 *
 * @author oliver.guenther
 */
public class DocumentEao extends AbstractEao<Document> {

    private EntityManager em;

    public DocumentEao(EntityManager em) {
        super(Document.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns all active Documents by Directive.
     *
     * @param directive the directive
     * @return all active Documents by Directive.
     */
    public List<Document> findActiveByDirective(Document.Directive directive) {
        return em.createNamedQuery("Document.findActiveByDirective", Document.class).setParameter(1, directive).getResultList();
    }

    /**
     * Returns all CreditMemos with {@link Directive#NONE} which are not closed.
     *
     * @return all CreditMemos with {@link Directive#NONE} which are not closed.
     */
    public List<Document> findCloseableCreditMemos() {
        return em.createNamedQuery("Document.activeOpenByTypeDirective", Document.class).setParameter(1, DocumentType.CREDIT_MEMO).setParameter(2, Document.Directive.NONE).getResultList();
    }

    /**
     * Returns all Documents of {@link Document#type} between two Dates.
     *
     * @param start the start Date
     * @param end   the end Date
     * @param types the searched {@link Document#type}
     * @return all Documents of {@link Document#type} between two Dates.
     */
    public List<Document> findDocumentsBetweenDates(Date start, Date end, DocumentType... types) {
        return em.createNamedQuery("Document.betweenDates", Document.class).setParameter(1, start).setParameter(2, end).setParameter(3, Arrays.asList(types)).getResultList();
    }

    /**
     * Returns the newest active document of the news dossier, which is still open by customer id.
     * <p/>
     * @param type       the type
     * @param customerId the customerId
     * @return the newest active document of the news dossier, which is still open by customer id, null if non found.
     */
    public Document findActiveAndOpenByCustomerId(DocumentType type, long customerId) {
        try {
            return em.createNamedQuery("Document.findActiveAndOpenByCustomerId", Document.class)
                    .setParameter(1, type).setParameter(2, customerId).setMaxResults(1).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Get the documents where the identifier matches the search.
     * <p/>
     * @param search the searched identifier
     * @return the documents where the identifier matches the search.
     */
    public List<Document> findByIdentifierAndType(String search, DocumentType type) {
        TypedQuery<Document> query = em.createNamedQuery("Document.byIdentifier", Document.class);
        query.setParameter(1, (search.contains("*") ? search.replace("*", "%") : search.replace("*", "")).toUpperCase());
        query.setParameter(2, type);
        return query.getResultList();
    }

    /**
     * Get the documents where the dossier has the {@link PaymentMethod}, the type matches, and no {@link Document.Condition} PAID is not set.
     * <p/>
     * @param paymentMethod The paymentMethod
     * @return documents where the dossier has the {@link PaymentMethod}, the type matches, and no {@link Document.Condition} PAID is not set.
     */
    public List<Document> findOpenInvoiceUnpaidByTypePaymentMethod(PaymentMethod paymentMethod) {
        List<Document> docs = em.createNamedQuery("Document.findOpenInvoiceUnpaidByTypePaymentMethod", Document.class)
                .setParameter(1, DocumentType.INVOICE)
                .setParameter(2, paymentMethod).getResultList();
        for (Document document : new ArrayList<>(docs)) {
            if ( document.getConditions().contains(Condition.PAID) ) docs.remove(document);
        }
        return docs;
    }

    /**
     * Get the documents of type ANULATION_INVOICE and CREDIT_MEMO where customerId matches, paymentMethod is DIRECT_DEBIT and directive is BALANCE_REPAYMENT.
     * <p/>
     * @param customerId The customerId that has to match with the document.dossier.customerId.
     * @return documents of type ANULATION_INVOICE and CREDIT_MEMO where customerId matches, paymentMethod is DIRECT_DEBIT and directive is BALANCE_REPAYMENT.
     */
    public List<Document> findOpenAnulationByCustomerPaymentMethod(long customerId) {
        return em.createNamedQuery("Document.findOpenAnulationByCustomerPaymentMethod", Document.class)
                .setParameter(1, customerId)
                .setParameter(2, Arrays.asList(new DocumentType[]{DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO}))
                .setParameter(3, PaymentMethod.DIRECT_DEBIT)
                .setParameter(4, Document.Directive.BALANCE_REPAYMENT)
                .getResultList();
    }

    /**
     * Returns all active Documents of type Invoice, which have a position with the supplied uniqueunit.productid.
     * <p/>
     * @param productId the productId of UniqueUnit
     * @return all active Documents of type Invoice, which have a position with the supplied uniqueunit.productid.
     */
    public List<Document> findInvoiceWithProdcutId(long productId) {
        return em.createNamedQuery("Document.productIdAndType", Document.class)
                .setParameter(1, productId)
                .setParameter(2, DocumentType.INVOICE)
                .getResultList();
    }
}
