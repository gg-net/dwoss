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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.Map.Entry;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.uniqueunit.api.event.UnitHistory;
import eu.ggnet.dwoss.mandator.api.service.WarrantyService;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.redtapext.ee.workflow.RedTapeWorkflow;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.saft.api.progress.IMonitor;
import eu.ggnet.statemachine.State.Type;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Condition.*;
import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.rules.DocumentType.BLOCK;
import static eu.ggnet.dwoss.common.api.values.PaymentMethod.*;
import static eu.ggnet.dwoss.rules.PositionType.COMMENT;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CONTRACTOR_REFERENCE;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.MANUFACTURER_COST;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

/**
 * Operation for closing of RedTape.
 *
 * @author oliver.guenther
 */
@Singleton
public class RedTapeCloserOperation implements RedTapeCloser {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final static Logger L = LoggerFactory.getLogger(RedTapeCloserOperation.class);

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private LogicTransactionEao ltEao;

    @Inject
    private StockEao stockEao;

    @Inject
    private StockUnitEao suEao;

    @Inject
    private StockTransactionEmo stEmo;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Event<UnitHistory> history;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private Instance<WarrantyService> warrantyServiceInstance;

    @Inject
    private ReceiptCustomers receiptCustomers;

    /**
     * Executes the closing manual.
     * See {@link #closeing(java.lang.String, boolean) } for details.
     * <p>
     * @param arranger the arranger
     */
    @Override
    public void executeManual(String arranger) {
        closeing(arranger, true);
    }

    /**
     * Exectues the closing automatic.
     * See {@link #closeing(java.lang.String, boolean) } for details.
     * <p>
     */
    @Schedule(hour = "22") // This kicks in exaclty once a day
    @Override
    public void executeAutomatic() {
        closeing("scheduler (automatic)", false);
    }

    /**
     * The closing selects and closes all dossiers and documents in a closable state. This includes production of appropriated values for report.
     * <p/>
     * The workflow:
     * <ol>
     * <li>Load all open {@link Dossiers}, and filter them by the closing states of the associated {@link Documents}.See
     * {@link #findReportable(de.dw.progress.IMonitor)}</li>
     * <li>Filter out all Document, which have {@link StockUnit}'s on open {@link StockTransaction}.</li>
     * <li>Create the resulting {@link ReportLine}'s and persist them in the report database. See
     * {@link #poluteReporting(java.util.Set, java.util.Date, de.dw.progress.IMonitor)} </li>
     * <li>Close the selected {@link Dossier}'s and {@link Document}'s in redTape. See {@link #closeRedTape(java.util.Set, de.dw.progress.IMonitor)}</li>
     * <li>Find all associated {@link StockUnit}'s and roll them out. See
     * {@link #closeStock(java.util.Set, java.lang.String, java.lang.String, de.dw.progress.IMonitor)}</li>
     * </ol>
     * <p>
     * <p/>
     * @param arranger the arranger
     * @param manual   is this called manual or automatic
     */
    private void closeing(String arranger, boolean manual) {
        Date now = new Date();
        String msg = (manual ? "Manueller" : "Automatischer") + " (Tages)abschluss vom " + DateFormats.ISO.format(now) + " ausgeführt durch " + arranger;
        L.info("closing:{}", msg);
        SubMonitor m = monitorFactory.newSubMonitor((manual ? "Manueller" : "Automatischer") + " (Tages)abschluss", 100);
        m.start();

        L.info("closing:week persisted");
        Set<Document> reportable = findReportable(m.newChild(30));
        L.info("closing:documents selected");
        reportable = filterOpenStockTransactions(reportable, m.newChild(5));
        L.info("closing:documents filtered");

        poluteReporting(reportable, DateUtils.truncate(new Date(), Calendar.DATE), m.newChild(15));
        L.info("closing:repoting poluted");

        closeRedTape(reportable, m.newChild(10));
        L.info("closed:redTape:reportables");

        closeRedTape(findCloseableBlocker(), m.newChild(10));
        L.info("closed:redTape:nonreportables");

        closeStock(reportable.stream()
                .map(Document::getDossier)
                .map(Dossier::getId)
                .collect(Collectors.toSet()),
                "Rollout by " + (manual ? "manuel" : "automatic") + " closing on " + DateFormats.ISO.format(now), arranger, m);
        L.info("closed:stock");

        m.finish();
    }

    /**
     * Closes the Documents and it's dossiers. This simply sets {@link Document#closed} and {@link Dossier#closed} to true.
     * For information on reopening {@link Dossier}'s see {@link RedTapeWorkflow#refreshAndPrepare(de.dw.redtape.entity.Document, de.dw.redtape.entity.Document)
     * }.
     * <p/>
     * @param documents the documents to close
     * @param monitor   a optional monitor.
     * @return a container instance with dossierIds to be closed and/or reopened in sopo.
     */
    private void closeRedTape(Set<Document> documents, IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor, documents.size());
        m.start();
        for (Document document : documents) {
            m.worked(1, " verbuche " + document.getIdentifier());
            document.setClosed(true);
            document.getDossier().setClosed(true);
        }
        m.finish();
    }

    /**
     * Rolling out the units from the stocks. For this, a {@link StockTransaction} with {@link StockTransactionType#ROLL_OUT} is created,
     * all {@link StockUnit}<code>s</code> which are on a {@link LogicTransaction} with matching dossierId are added to this {@link StockTransaction} and
     * it gets {@link StockTransactionStatusType#COMPLETED}.
     * <p/>
     * @param dossierIds the dossierId as reference.
     * @param msg        a msg for the stocktransaction.
     * @param arranger   the arranger.
     * @param monitor    a optional monitor.
     * @return the amount of rolled out units.
     */
    private int closeStock(Set<Long> dossierIds, String msg, String arranger, IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor, 100);
        final String h = "Stock:";
        m.message(h + "lade logische Transaktionen");
        // Rolling out
        List<LogicTransaction> lts = ltEao.findByDossierIds(dossierIds);
        m.worked(3, h + "sortiere Geräte nach Lager");
        stockEao.findAll();
        Map<Stock, List<StockUnit>> unitsByStock = lts.stream().flatMap((t) -> t.getUnits().stream()).collect(Collectors.groupingBy(StockUnit::getStock));
        validateStockUnits(unitsByStock);

        m.setWorkRemaining((int)unitsByStock.values().stream().count());
        List<StockTransaction> stockTransactions = new ArrayList<>();
        for (Entry<Stock, List<StockUnit>> entry : unitsByStock.entrySet()) {
            StockTransaction st = stEmo.requestRollOutPrepared(entry.getKey().getId(), arranger, msg);
            for (StockUnit stockUnit : entry.getValue()) {
                m.worked(1, h + "verbuche (refurbishId=" + stockUnit.getRefurbishId() + ",uniqueUnitId=" + stockUnit.getUniqueUnitId() + ")");
                st.addUnit(stockUnit);
                history.fire(new UnitHistory(stockUnit.getUniqueUnitId(), msg, arranger));
            }
            stockTransactions.add(st);

        }
        m.message(h + "auslagern");
        if ( !stockTransactions.isEmpty() ) stEmo.completeRollOut(arranger, stockTransactions);
        m.finish();
        return (int)unitsByStock.values().stream().count();
    }

    private void validateStockUnits(Map<Stock, List<StockUnit>> unitsByStock) {
        // Duplicated Validation, safetynet.
        if ( unitsByStock.containsKey(null) ) throw new RuntimeException("StockUnits have no Stock, on Transaction ? : " + unitsByStock.get(null));
        unitsByStock.values().stream().flatMap(l -> l.stream()).forEach((StockUnit u) -> {
            if ( !validator.validate(u).isEmpty() ) throw new ConstraintViolationException("Invalid StockUnit " + u,
                        new HashSet<>(validator.validate(u)));
        });
    }

    /**
     * Filters out all {@link Document}'s with associated {@link StockUnit}'s on an open {@link StockTransaction}.
     * See {@link StockTransactionStatusType} and {@link StockTransaction#POSSIBLE_STATUS_TYPES} for details about open {@link StockTransaction}.
     * <p>
     * <p/>
     * @param documents the documents as reference
     * @param monitor   a optional monitor
     * @return all documents which are not on open transactions.
     */
    private Set<Document> filterOpenStockTransactions(Set<Document> documents, IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor, documents.size());
        m.start();
        m.message(" filtere");
        for (Iterator<Document> it = documents.iterator(); it.hasNext();) {
            Document document = it.next();
            m.worked(1, " filtere " + document.getIdentifier());
            LogicTransaction lt = ltEao.findByDossierId(document.getDossier().getId());
            if ( lt == null ) continue;
            for (StockUnit stockUnit : lt.getUnits()) {
                if ( !validator.validate(stockUnit).isEmpty() || stockUnit.isInTransaction() || stockUnit.getStock() == null ) {
                    it.remove();
                    L.warn("Closing: The Dossier(id={},customerId={}) has the Unit(refurbhisId={})"
                            + " which is in an invalid state (validation error,open StockTransaction), excluding Dossier from closing.",
                            document.getDossier().getId(), document.getDossier().getCustomerId(), stockUnit.getRefurbishId());
                    break;
                }
            }
        }
        m.finish();
        return documents;
    }

    /**
     * Discovers all Documents, which are in closing state.
     * <p>
     * Closing States are:
     * <table border="1" >
     * <thead>
     * <tr><th>Case</th><th>Document Type</th><th>PaymentMethod</th><th>Conditions</th><th>Directive</th></tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>1</td>
     * <td>{@link Type#ORDER}</td>
     * <td>*</td>
     * <td>{@link Condition#CANCELED}</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>{@link Type#INVOICE} overwrites {@link Type#ORDER}</td>
     * <td>{@link PaymentMethod#ADVANCE_PAYMENT}</td>
     * <td>{@link Condition#PAID} &amp; ( {@link Condition#SENT} | {@link Condition#PICKED_UP} )</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>{@link Type#INVOICE} overwrites {@link Type#ORDER}</td>
     * <td>{@link PaymentMethod#CASH_ON_DELIVERY}</td>
     * <td>{@link Condition#SENT}</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>{@link Type#INVOICE} overwrites {@link Type#ORDER}</td>
     * <td>{@link PaymentMethod#DIRECT_DEBIT}</td>
     * <td>{@link Condition#SENT} | {@link Condition#PICKED_UP}</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>5</td>
     * <td>{@link Type#INVOICE} overwrites {@link Type#ORDER}</td>
     * <td>{@link PaymentMethod#INVOICE}</td>
     * <td>{@link Condition#SENT} | {@link Condition#PICKED_UP}</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>6</td>
     * <td>{@link Type#ANNULATION_INVOICE} | {@link Type#CREDIT_MEMO}</td>
     * <td>*</td>
     * <td>*</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>7</td>
     * <td>{@link Type#COMPLAINT}</td>
     * <td>*</td>
     * <td>*</td>
     * <td>*</td>
     * </tr>
     * <tr>
     * <td>8</td>
     * <td>{@link Type#CAPITAL_ASSET} | {@link Type#RETURNS}</td>
     * <td>*</td>
     * <td>{@link Condition#PICKED_UP}</td>
     * <td>{@link Directive#NONE}</td>
     * </tr>
     * </tbody>
     * </table>
     * Comments:
     * <ul>
     * <li>A canceled order will be closed, but not reported.</li>
     * <li>A complaint is a very special case, which might be closed two times. See {@link  RedTapeWorkflow#refreshAndPrepare(de.dw.redtape.entity.Document, de.dw.redtape.entity.Document)
     * }</li>
     * </ul>
     * <p>
     * @param monitor a optional monitor.
     * @return all documents, which are in a closing state.
     */
    private Set<Document> findReportable(IMonitor monitor) {
        SubMonitor m = SubMonitor.convert(monitor);
        m.start();
        m.message(" lade offene Vorgänge");

        List<Dossier> openDossiers = new DossierEao(redTapeEm).findByClosed(false);
        m.worked(5);
        m.setWorkRemaining(openDossiers.size());
        Set<Document> closeable = new HashSet<>();

        for (Dossier dossier : openDossiers) {
            m.worked(1, " selecting " + dossier.getIdentifier());

            // Check if there is only an order.
            if ( dossier.getActiveDocuments().size() == 1 && dossier.getActiveDocuments(DocumentType.ORDER).size() == 1 ) {
                Document doc = dossier.getActiveDocuments(DocumentType.ORDER).get(0);
                if ( doc.getConditions().contains(CANCELED) ) closeable.add(doc);
                L.debug("Filtered not reportable {}, cause: canceled order", doc.getDossier().getIdentifier());
                // Shortcut: If there is only an order, that is not canceled. we do not close it. If it is canceled, we close it.
                continue;
            }
            // Check the Closing State. Every closable document is removed from the copied collection.
            // If the collection is empty at the end, the dossier can be closed, meaning we remove all cases that we consider closing state.
            List<Document> activeDocuments = new ArrayList<>(dossier.getActiveDocuments());
            for (Iterator<Document> it = activeDocuments.iterator(); it.hasNext();) {
                Document document = it.next();
                Set<Condition> conditions = document.getConditions();
                if ( document.isClosed() ) {
                    it.remove();
                    // At this point a ORDER is never the only Document, therfore we safly ignore it.
                    // All Repayments get reported on creation.
                } else if ( document.getType() == DocumentType.ORDER || document.getType() == DocumentType.ANNULATION_INVOICE || document.getType() == DocumentType.CREDIT_MEMO ) {
                    it.remove();
                } else if ( document.getType() == DocumentType.INVOICE ) {
                    switch (dossier.getPaymentMethod()) {
                        case ADVANCE_PAYMENT:
                            if ( conditions.contains(PAID) && (conditions.contains(SENT) || conditions.contains(PICKED_UP)) ) it.remove();
                            break;
                        case CASH_ON_DELIVERY:
                            if ( conditions.contains(SENT) ) it.remove();
                            break;
                        case DIRECT_DEBIT:
                            if ( conditions.contains(SENT) || conditions.contains(PICKED_UP) ) it.remove();
                            break;
                        case INVOICE:
                            if ( conditions.contains(SENT) || conditions.contains(PICKED_UP) ) it.remove();
                            break;
                    }
                } else if ( document.getType() == DocumentType.CAPITAL_ASSET || document.getType() == DocumentType.RETURNS ) {
                    if ( conditions.contains(PICKED_UP) ) it.remove();
                } else if ( document.getType() == DocumentType.COMPLAINT ) {
                    // A Complaint gets allways closed. See RedTapeWorkflow.refreshAndPrepare() for the reopening conditions.
                    it.remove();
                }
                // TODO: There might be a special case, that someone made the CreditMemo on the Invoice, but had a Complait before.
                // We should cleanup this also. See: http://overload.ahrensburg.gg-net.de/jira/browse/DW-831
            }
            // Empty means, all documents in a dossier are either closed or in a closing state, expect Blocks and orders.
            if ( activeDocuments.isEmpty() ) {
                for (Document document : dossier.getActiveDocuments()) {
                    if ( document.getType() == DocumentType.ORDER ) continue; // The Order part is never Reported.
                    if ( document.getType() == DocumentType.BLOCK ) continue; // Should never happen, no concept jet.
                    if ( document.isClosed() ) continue; // Don't close it twice
                    closeable.add(document);
                }
            } else if ( L.isDebugEnabled() ) {
                List<String> shorts = new ArrayList<>();
                String identifier = activeDocuments.get(0).getIdentifier();
                for (Document document : activeDocuments) {
                    shorts.add(document.toTypeConditions());
                }
                L.debug("Filtered not reportable {}, cause: Not closeable documents: {}", identifier, shorts);
            }
        }
        m.finish();
        return closeable;
    }

    private Set<Document> findCloseableBlocker() {
        Collection<Long> receipts = receiptCustomers.getReceiptCustomers().values();
        List<Dossier> openDossiers = new DossierEao(redTapeEm).findByClosed(false)
                .stream().filter(d -> !receipts.contains(d.getCustomerId())).collect(Collectors.toList());

        //all active blockers from open dossiers
        Set<Document> blocker = openDossiers.stream()
                .filter(d -> !d.getActiveDocuments(BLOCK).isEmpty())
                .map(d -> d.getActiveDocuments(BLOCK))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        //directly closable, only comment positions
        Set<Document> closable = blocker.stream()
                .filter(d -> d.getPositions().values().stream().allMatch(p -> p.getType() == COMMENT))
                .collect(Collectors.toSet());

        //documents containing at least one unit type position
        Set<Document> containsUnit = blocker.stream()
                .filter(d -> d.getPositions().values().stream().anyMatch(p -> p.getType() == UNIT))
                .collect(Collectors.toSet());

        //remove all documents where at least one unit is still in stock
        containsUnit.removeIf(
                d -> d.getPositions().values().stream().anyMatch(p -> p.getType() == UNIT && suEao.findByUniqueUnitId(p.getUniqueUnitId()) != null));

        closable.addAll(containsUnit);

        return closable;
    }

    /**
     * Actually creating reportLines from the reportable documents.
     * For each position of a {@link Document} a {@link ReportLine} is created with all information supplied.
     * <p>
     * Exceptions are:
     * <ul>
     * <li>A {@link Document} with a {@link Condition#CANCELED} which is silently ignored</li>
     * <li>A {@link Document} with a {@link DocumentType#COMPLAINT} which sets all prices on the {@link ReportLine} to 0 and
     * <ul>
     * <li>If the {@link Document} has the {@link Condition#WITHDRAWN} or {@link Condition#REJECTED}, set {@link ReportLine#workflowStatus} to
     * {@link ReportLine.WorkflowStatus#DISCHARGED}</li>
     * <li>If the {@link Document} has the {@link Condition#ACCEPTED}, set {@link ReportLine#workflowStatus} to
     * {@link ReportLine.WorkflowStatus#CHARGED}</li>
     * <li>Otherwise set {@link ReportLine#workflowStatus} to {@link ReportLine.WorkflowStatus#UNDER_PROGRESS}</li>
     * </ul>
     * </li>
     * <li>A {@link Document} with a {@link DocumentType#CREDIT_MEMO} gets its prices inverted</li>
     * </ul>
     * <p/>
     * @param reportable the documents to create lines from
     * @param monitor    a optional monitor.
     * @return the amount of created lines.
     */
    private int poluteReporting(Set<Document> reportable, Date reporting, IMonitor monitor) {
        WarrantyService warrantyService = null;
        if ( !warrantyServiceInstance.isUnsatisfied() ) {
            warrantyService = warrantyServiceInstance.get();
        }

        SubMonitor m = SubMonitor.convert(monitor, reportable.size() + 10);
        m.start();
        ReportLineEao reportLineEao = new ReportLineEao(reportEm);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        ProductEao productEao = new ProductEao(uuEm);
        int amountCreate = 0;
        List<ReportLine> newLines = new ArrayList<>(reportable.size());
        for (Document document : reportable) {
            m.worked(1, "reported " + document.getIdentifier());
            // A canceled document must be closed, but must not create a reportline.
            if ( document.getConditions().contains(Condition.CANCELED) ) continue;

            ReportLine l;
            for (Position position : document.getPositions().values()) {
                amountCreate++;
                l = new ReportLine();
                l.setActual(document.getActual());
                l.setAmount(position.getAmount());
                l.setBookingAccount(position.getBookingAccount().map(Ledger::getValue).orElse(-1));
                l.setCustomerId(document.getDossier().getCustomerId());
                l.setDescription(normalizeSpace(position.getDescription()));
                l.setDocumentId(document.getId());
                l.setDocumentIdentifier(document.getIdentifier());
                l.setDocumentType(document.getType());
                l.setDossierId(document.getDossier().getId());
                l.setDossierIdentifier(document.getDossier().getIdentifier());
                // TODO: We could use something else for a separator, but keep in mind that we want to avoid name, , , something.
                l.setInvoiceAddress(normalizeSpace(document.getInvoiceAddress().getDescription()));
                l.setName(normalizeSpace(position.getName()));
                l.setPositionType(position.getType());
                l.setPrice(position.getPrice());
                l.setReportingDate(reporting);
                l.setTax(position.getTax());

                l.setMarginPercentage(0); // Set via Report afterwards
                l.setPurchasePrice(0); // Set via Report afterwards

                UiCustomer c = customerService.asUiCustomer(document.getDossier().getCustomerId());
                if ( c != null ) {
                    l.setCustomerCompany(c.getCompany());
                    l.setCustomerName(c.toTitleNameLine());
                    l.setCustomerEmail(c.getEmail());
                }
                // A Credit Memo gets its prices inverted
                if ( document.getType() == DocumentType.CREDIT_MEMO ) {
                    l.setPrice(position.getPrice() * (-1));
                }

                // Special handling of complaints.
                if ( document.getType() == DocumentType.COMPLAINT ) {
                    // A Complaint position has "tagging" effect, but shall never result in a plus or minus.
                    l.setPrice(0);
                    if ( document.getConditions().contains(Condition.REJECTED) || document.getConditions().contains(Condition.WITHDRAWN) ) {
                        l.setWorkflowStatus(ReportLine.WorkflowStatus.DISCHARGED);
                    } else if ( document.getConditions().contains(Condition.ACCEPTED) ) {
                        l.setWorkflowStatus(ReportLine.WorkflowStatus.CHARGED);
                    } else {
                        l.setWorkflowStatus(ReportLine.WorkflowStatus.UNDER_PROGRESS);
                    }
                }

                // Extra information for Type Position
                if ( position.getType() == PositionType.UNIT || position.getType() == PositionType.UNIT_ANNEX ) {
                    UniqueUnit uu = Objects.requireNonNull(uniqueUnitEao.findById(position.getUniqueUnitId()),
                            "No UniqueUnit with id=" + position.getUniqueUnitId());
                    Product p = uu.getProduct();
                    if ( uu.getContractor() == p.getTradeName().getManufacturer() ) {
                        l.setContractorPartNo(p.getPartNo());
                        l.setContractorReferencePrice(p.getPrice(MANUFACTURER_COST));
                    } else {
                        l.setContractorPartNo(p.getAdditionalPartNo(uu.getContractor()));
                        l.setContractorReferencePrice(p.getPrice(CONTRACTOR_REFERENCE));
                    }
                    l.setManufacturerCostPrice(p.getPrice(MANUFACTURER_COST));
                    l.setContractor(uu.getContractor());
                    l.setContractorReferencePrice(p.getPrice(CONTRACTOR_REFERENCE));
                    if ( Math.abs(l.getContractorReferencePrice()) < 0.001 ) l.setContractorReferencePrice(p.getPrice(MANUFACTURER_COST));
                    l.setMfgDate(uu.getMfgDate());
                    l.setRefurbishId(uu.getRefurbishId());
                    l.setSerial(uu.getSerial());
                    l.setUniqueUnitId(uu.getId());
                    l.setSalesChannel(uu.getSalesChannel());

                    l.setPartNo(p.getPartNo());
                    l.setProductBrand(p.getTradeName());
                    l.setProductName(p.getName());
                    l.setProductGroup(p.getGroup());
                    l.setProductId(p.getId());
                    l.setGtin(p.getGtin());
                    // Extra Information for Type Product Batch
                } else if ( position.getType() == PositionType.PRODUCT_BATCH ) {
                    Product p = Objects.requireNonNull(productEao.findById(position.getUniqueUnitProductId()),
                            "No Product for id=" + position.getUniqueUnitProductId());

                    l.setPartNo(p.getPartNo());
                    l.setProductBrand(p.getTradeName());
                    l.setProductGroup(p.getGroup());
                    l.setProductName(p.getName());
                    l.setProductId(p.getId());
                    l.setGtin(p.getGtin());
                    l.setUniqueUnitId(position.getUniqueUnitId());
                    l.setSerial(position.getSerial());
                    l.setRefurbishId(position.getRefurbishedId());

                    if ( warrantyService != null ) {
                        l.setContractor(warrantyService.warrantyContractor(p.getPartNo())); // If this is no warranty Partno, this will return null ;-)
                    }
                }
                reportEm.persist(l);
                newLines.add(l);
            }
        }

        reportEm.flush();

        m.message("Updateing References");
        for (ReportLine newLine : newLines) {
            if ( newLine.getUniqueUnitId() < 1 ) continue; // Not Refs for Product_Batches or Versandkosten jet.
            if ( newLine.getPositionType() == PositionType.PRODUCT_BATCH ) {
                // TODO: also evaluate the productId.
                newLine.addAll(reportLineEao.findBySerialAndPositionTypeAndDossierId(newLine.getSerial(), newLine.getPositionType(), newLine.getDossierId()));
            } else {
                newLine.addAll(reportLineEao.findUnitsAlike(newLine.getUniqueUnitId(), newLine.getDossierId()));
            }
        }

        updateSingleReferences(newLines);

        m.worked(5);
        m.finish();
        return amountCreate;
    }

    /**
     * Sets the single References.
     * <p>
     * @param lines
     */
    void updateSingleReferences(Collection<ReportLine> lines) {
        if ( warrantyServiceInstance.isUnsatisfied() ) return;
        for (ReportLine line : lines) {
            if ( !warrantyServiceInstance.get().isWarranty(line.getPartNo()) ) continue;
            for (ReportLine unit : lines) {
                if ( unit.getPositionType() != UNIT ) continue;
                if ( line.getDocumentId() == unit.getDocumentId()
                        && line.getWorkflowStatus() == unit.getWorkflowStatus()
                        && StringUtils.equals(line.getRefurbishId(), unit.getRefurbishId()) ) {
                    unit.setReference(WARRANTY, line);
                }
            }
        }

    }
}
