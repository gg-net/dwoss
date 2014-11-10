package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.redtape.entity.Address;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.*;

import eu.ggnet.dwoss.event.AddressChange;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.format.DossierFormater;


import eu.ggnet.statemachine.State.Type;

import eu.ggnet.statemachine.StateTransition;

import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.entity.StockUnit;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

import eu.ggnet.dwoss.util.UserInfoException;

import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.dwoss.redtape.state.RedTapeStateMachine;
import eu.ggnet.dwoss.redtape.workflow.*;

import static eu.ggnet.dwoss.rules.DocumentType.*;

/**
 * This class handles every operation between RedTape, UniqueUnit, Sopo and Stock.<br />
 * <u>For Dossier creation both:</u><br />
 * {@link RedTapeWorkerOperation#create(de.dw.rules.PaymentMethod, boolean, int, java.lang.String)} as well as <br />
 * {@link RedTapeWorkerOperation#update(de.dw.redtape.entity.Document, java.lang.String)} is needed. <br />
 * create() will only handle RedTapeTransactions while update will handle all necessary SopoTransactions.
 * <p/>
 * @author pascal.perau
 */
@Stateless
public class RedTapeWorkerOperation implements RedTapeWorker {

    private static final Logger L = LoggerFactory.getLogger(RedTapeWorkerOperation.class);

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private AddressService addressService;

    @Inject
    private RedTapeUpdateRepaymentWorkflow repaymentWorkflow;

    @Inject
    private RedTapeCreateDossierWorkflow createDossierWorkflow;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private Mandator mandator;

    @Inject
    private CustomerService customerService;

    private final RedTapeStateMachine stateMachine = new RedTapeStateMachine();

    @Override
    public List<StateTransition<CustomerDocument>> getPossibleTransitions(CustomerDocument cdoc) {
        return stateMachine.getPossibleTransitions(cdoc);
    }

    /**
     * Creates a new SalesProduct for the PartNo.
     * <p/>
     * @param partNo the partNo to use.
     * @return the new SalesProduct.
     * @throws UserInfoException if the PartNo does not exist.
     */
    @Override
    public SalesProduct createSalesProduct(String partNo) throws UserInfoException {
        ProductEao productEao = new ProductEao(uuEm);
        Product findByPartNo = productEao.findByPartNo(partNo);
        if ( findByPartNo == null ) throw new UserInfoException("Part Nummer exestiert Nicht!");
        SalesProduct salesProduct = new SalesProduct(partNo, findByPartNo.getName(), findByPartNo.getPrice(PriceType.SALE), findByPartNo.getId(), findByPartNo.getDescription());
        redTapeEm.persist(salesProduct);
        return salesProduct;
    }

    /**
     * Creates a new, valid Dossier containing a document of the order type.
     *
     * See {@link RedTapeCreateDossierWorkflow} for implementation.
     *
     * @param customerId The customer associated to the new Dossier.
     * @param dispatch   The dispatch state of the Dossier.
     * @param arranger   The arranger of the new Dossier.
     * @return A new, valid, persisted Dossier.
     */
    @Override
    public Dossier create(long customerId, boolean dispatch, String arranger) {
        return createDossierWorkflow.execute(customerId, dispatch, arranger);
    }

    /**
     * This method handles necessary cleanups if creation or update is canceled.
     * <p/>
     * If stock.LogicTransaction differs form Dossier.Document.Positions &rArr; change LogicTransaction.
     * <ul><li>Only LogicTransaction > Dossier.Document.Positions should be possible</li></ul>
     * If Dossier.isEmpty &rarr; delete.
     * If stock.LogicTransaction is empty &rarr; delete.
     */
    @Override
    public Document revertCreate(Document detached) throws UserInfoException {
        Document original = new DocumentEao(redTapeEm).findById(detached.getId());
        if ( original.isActive() != detached.isActive() )
            throw new UserInfoException("Das Document wurde durch jemand anderen inzwischen geändert, bitte neu laden.\n"
                    + "Hint: original(" + original.getId() + ").active=" + original.isActive() + ", detached(" + detached.getId() + ").active=" + detached.isActive());
        if ( original.getOptLock() != detached.getOptLock() )
            throw new UserInfoException("Das Document wurde durch jemand anderen inzwischen geändert, bitte neu laden.\n"
                    + "Hint: original(" + original.getId() + ").optLock=" + original.getOptLock() + ", detached(" + detached.getId() + ").optLock=" + detached.getOptLock());
        LogicTransactionEmo ltEmo = new LogicTransactionEmo(stockEm);
        if ( !detached.isClosed() )
            ltEmo.equilibrate(original.getDossier().getId(), original.getPositionsUniqueUnitIds());

        if ( original.getDossier().getDocuments().size() == 1 && original.getPositions().isEmpty() ) {
            redTapeEm.remove(original.getDossier());
            return null;
        }
        return original;
    }

    /**
     * Gives a pair of {@link Address}es based on the Customer.
     * <p/>
     * If no Address could be found, new persisted enteties are created.
     * <p/>
     */
    @Override
    public Addresses requestAdressesByCustomer(long customerId) {
        AddressEmo addressEmo = new AddressEmo(redTapeEm);
        Address invoice = addressEmo.request(addressService.defaultAddressLabel(customerId, AddressType.INVOICE));
        Address shipping = addressEmo.request(addressService.defaultAddressLabel(customerId, AddressType.SHIPPING));
        return new Addresses(invoice, shipping);
    }

    /**
     * Gives either an address out of the db or persist a new one if nothing is found.
     * <p/>
     * @param description
     * @return the found or new persisted Address
     */
    @Override
    public Address requestAddressByDescription(String description) {
        return new AddressEmo(redTapeEm).request(description);
    }

    /**
     * Update Comments of the Dossier.
     * <p/>
     * @param dossier the dossier to update
     * @param comment the comment
     * @return returns the dossier
     * @throws UserInfoException if something is not ok.
     */
    @Override
    public Dossier updateComment(Dossier dossier, String comment) throws UserInfoException {
        if ( Objects.equals(dossier.getComment(), comment) ) return dossier;
        long id = dossier.getId();
        // This is some magic to find out if this is are SopoAuftrag.
        if ( id == 0 ) throw new UserInfoException("Dossier is not in Database, but updateComment is called");
        dossier = new DossierEao(redTapeEm).findById(dossier.getId());
        if ( dossier == null ) throw new UserInfoException("Dossier(id=" + id + ") is not in Database, but updateComment is called");
        dossier.fetchEager();
        dossier.setComment(comment);
        return dossier;
    }

    /**
     * Create a HTML formated String representing the detailed information from a {@link Dossier} and the Sopo data belonging to the Dossier.
     * <p/>
     * @param dossierId The Dossier
     * @return a HTML formated String representing the detailed information from a {@link Dossier} and the Sopo data belonging to the Dossier.
     */
    @Override
    public String toDetailedHtml(long dossierId) {
        Dossier dos = redTapeEm.find(Dossier.class, dossierId);
        if ( dos == null ) return "<strong>No Dossier with Id: " + dossierId + " found</strong>";
        String detailedHtmlCustomer = customerService.asHtmlHighDetailed(dos.getCustomerId());
        String stockInfo = "StockUnits:<ul>";
        for (StockUnit stockUnit : new StockUnitEao(stockEm).findByUniqueUnitIds(dos.getRelevantUniqueUnitIds()))
            stockInfo += "<li>" + stockUnit + "</li>";
        stockInfo += "</ul>";
        return "<html>" + detailedHtmlCustomer + "<br />"
                + DossierFormater.toHtmlDetailed(dos) + "<br />" + stockInfo + "</html>";
    }

    /**
     * Deletes a {@link Dossier}, cleaning up the Stock and if the Mandator uses Sopo,the referenced {@link Auftrag}.
     * <p/>
     * @param dos the Dossier to be deleted.
     */
    @Override
    public void delete(Dossier dos) {
        new LogicTransactionEmo(stockEm).equilibrate(dos.getId(), new ArrayList<Integer>());
        Dossier attachedDossier = new DossierEao(redTapeEm).findById(dos.getId());
        redTapeEm.remove(attachedDossier);
    }

    /**
     * Changes the {@link Address} of all active {@link Document} of {@link Type#ORDER} and no Invoices or CreditMemos found from every {@link Dossier}
     * containing a specific customer id.
     * <p/>
     * If one of the addresses does not exist, it will be created.
     * <p/>
     * @param addressChange
     */
    @Override
    public void updateAllDocumentAdresses(AddressChange addressChange) {
        AddressEmo addressEmo = new AddressEmo(redTapeEm);
        Address address = addressEmo.request(addressService.defaultAddressLabel(addressChange.getCustomerId(), addressChange.getType()));
        redTapeEm.detach(address);
        List<Dossier> dossiers = new DossierEao(redTapeEm).findByCustomerId(addressChange.getCustomerId());
        for (Dossier dossier : dossiers) {
            if ( !dossier.getActiveDocuments(DocumentType.INVOICE).isEmpty() ) continue;
            for (Document document : new HashSet<>(dossier.getActiveDocuments(DocumentType.ORDER))) {
                if ( document.getConditions().contains(Document.Condition.CANCELED) ) continue;
                // May be fetchEager
                redTapeEm.detach(document);
                if ( addressChange.getType() == AddressType.INVOICE ) document.setInvoiceAddress(address);
                if ( addressChange.getType() == AddressType.SHIPPING ) document.setShippingAddress(address);
                redTapeEm.flush();
                redTapeEm.clear();
                internalUpdate(document, null, addressChange.getArranger());
            }
        }
    }

    /**
     * Changes the State, of a Customer and Document based on the transition.
     * <p/>
     * @param cdoc       the Document and Customer to take the change.
     * @param transition the transition to do.
     * @param arranger   the arranger
     * @return the Document in the new state.
     */
    @Override
    public Document stateChange(CustomerDocument cdoc, StateTransition<CustomerDocument> transition, String arranger) {
        EnumSet<CustomerFlag> customerFlags = EnumSet.noneOf(CustomerFlag.class);
        customerFlags.addAll(cdoc.getCustomerFlags());
        L.info("stateChange with {} on {}", transition.getName(), cdoc);
        stateMachine.stateChange(cdoc, transition);
        // INFO: This is a stupid solution.
        if ( !customerFlags.equals(cdoc.getCustomerFlags()) )
            customerService.updateCustomerFlags(cdoc.getDocument().getDossier().getCustomerId(), cdoc.getCustomerFlags());
        return internalUpdate(cdoc.getDocument(), null, arranger);
    }

    /**
     * Update changes from a Document by looking up the original from the database.
     * <p/>
     * A document is not equal if {@link Document#equalsContentCreated the Block.(de.dw.redtape.entity.Document) } is false
     * or Document.getDossier.paymentMethod or Document.getDossier.dispatch are different.
     * Every Document manipulation is done by this method and handling all necessary manipulations in the SopoSoft system as well.
     * <p/>
     * <u>Dossier Handling</u>
     * <ul>
     * <li>Changes to {@link Dossier#paymentMethod} and {@link Dossier#dispatch} are persisted</li>
     * </ul>
     * <u>Document Handling</u>
     * <ul>
     * <li>If the given Document has no changes it is returned right away</li>
     * <li>If unequal a {@link Document#partialClone() } is used and detached Entities are attached (Dossier, Addresses)</li>
     * <li>If the {@link Document#getType() } is INVOICE while the previous version is not, a new Invoice Identifier is set to the new Document.</li>
     * </ul>
     * <u>SopoAuftrag Handling</u>
     * <ul>
     * <li>If the Document is updated, the SopoAuftrag will be updated as well.<br />
     * This is done by clearing all Positions and refill the whole SopoAuftrag.</li>
     * <li>If no SopoAuftrag exist, a new one is created.</li>
     * </ul>
     * <u>Stock Handling</u>
     * <ul>
     * <li>In any update process, the LogicTransaction will be cleared from its StockUnits</li>
     * <li>Does the new Document contain no Position of Position.Type.UNIT the LogicTransaction will be deleted.</li>
     * <li>Should there be any clash of StockUnit Transaction information, a Exception is thrown</li>
     * </ul>
     * <p/>
     *
     * @param doc         The Document that will be equalised against the original
     * @param destination In the case of CreditMemo, the destination for the units.
     * @param arranger    The recent user
     * @return A new persisted Document or the given if equal
     */
    @Override
    public Document update(final Document doc, Integer destination, final String arranger) {
        return internalUpdate(doc, destination, arranger);
    }

    private Document internalUpdate(final Document doc, Integer destination, final String arranger) {
        RedTapeWorkflow workflow = null;
        switch (doc.getType()) {
            case BLOCK:
                workflow = new RedTapeUpdateBlockWorkflow(
                        redTapeEm,
                        uuEm,
                        stockEm,
                        doc, arranger, mandator);
                break;
            case ORDER:
                workflow = new RedTapeUpdateOrderWorkflow(
                        redTapeEm,
                        uuEm,
                        stockEm,
                        doc, arranger, mandator);
                break;
            case INVOICE:
                workflow = new RedTapeUpdateInvoiceWorkflow(
                        redTapeEm,
                        uuEm,
                        stockEm,
                        doc, arranger, mandator);
                break;
            case RETURNS:
            case CAPITAL_ASSET:
                workflow = new RedTapeUpdateCapitalAssetReturnsWorkflow(
                        redTapeEm,
                        uuEm,
                        stockEm,
                        doc, arranger, mandator);
                break;
            case COMPLAINT:
                workflow = new RedTapeUpdateComplaintWorkflow(
                        redTapeEm,
                        uuEm,
                        stockEm,
                        doc, arranger, mandator);
                break;
            case CREDIT_MEMO:
            case ANNULATION_INVOICE:
                return fetchEager(repaymentWorkflow.execute(doc, destination, arranger));
            default:
        }
        Document result = workflow.execute();
        // TODO: Make a better fetch eager
        result.getDossier().getDocuments().size();
        return result;
    }

    private Document fetchEager(Document doc) {
        doc.getDossier().getDocuments().size();
        return doc;
    }
}
