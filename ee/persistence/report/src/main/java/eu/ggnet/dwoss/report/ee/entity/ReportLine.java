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
package eu.ggnet.dwoss.report.ee.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;
import eu.ggnet.dwoss.core.system.util.TwoDigits;
import eu.ggnet.dwoss.core.system.util.Utils;

import static eu.ggnet.dwoss.core.common.values.DocumentType.*;
import static eu.ggnet.dwoss.core.common.values.PositionType.*;
import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;

/**
 * This is a Line of a report. It could be represent any type of Position.
 * <p>
 * This is the documentation how a Report is filled with ReportLines:
 * TODO: Rewrite
 * <ol>
 * <li>Sammle alle Complaint Positionen raus die mit Repayment Positionen in vorhanden Reporten existieren → zu den allten Reporten</li>
 * <li>Sammle alle only Invoice Positions raus → Report</li>
 * <li>Sammle alle Repayment Positions raus → Report</li>
 * <li>Sammle alle Complaint Positionen die mit den Repayment Positionen zusammenhängen raus → Report</li>
 * <li>Sammle alle Compleints die DISCHARDED sind → Report</li>
 * <li>Alles was übrig ist, sollten (offene) Complaints sein → Active Info</li>
 * </ol>
 * This is the documentation how a Report is shown in the ReportViews:
 * Expects two sets of data: Report Data and Active Info
 * Has 4 views: Report View, Repayment View, Report Info, Active Info
 * <ol>
 * <li>Invoice only → Report Data</li>
 * <li>Repayment Unit + Invoice → Report Info</li>
 * <li>Repayment Unit_Annex + Invoice → Report Info + Repayment Data oder zusammenfassen mit info in ReportData</li>
 * <li>Repayment only → Repayment Data</li>
 * <li>Complaint Discharched + Invoice → Report Data + Report Info</li>
 * <li>Complaint Discharched Only → Report Info</li>
 * </ol>
 * <p>
 * @author bastian.venz, oliver.guenther
 */
@Entity
@NamedQuery(name = "ReportLine.allReverse", query = "SELECT r FROM ReportLine r ORDER BY r.reportingDate DESC, r.refurbishId ASC")
@NamedQuery(name = "ReportLine.byUniqueUnitId", query = "SELECT r FROM ReportLine r WHERE r.uniqueUnitId = ?1")
@NamedQuery(name = "ReportLine.byProductIdMissingContractorPartNo", query = "SELECT r FROM ReportLine r WHERE r.productId = ?1 and r.contractor = ?2 and r.contractorPartNo is null")
@NamedQuery(name = "ReportLine.byRefurbishId", query = "SELECT r FROM ReportLine r WHERE r.refurbishId = ?1")
@NamedQuery(name = "ReportLine.bySerialAndPositionTypeAndDossierId", query = "SELECT r FROM ReportLine r WHERE r.serial = ?1 AND r.positionType = ?2 AND r.dossierId = ?3")
@NamedQuery(name = "ReportLine.lastReported", query = "SELECT MAX(r.reportingDate) FROM ReportLine r")
@NamedQuery(name = "ReportLine.betweenDates", query = "SELECT r FROM ReportLine r WHERE r.reportingDate BETWEEN ?1 AND ?2")
@NamedQuery(name = "ReportLine.unreported", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)")
@NamedQuery(name = "ReportLine.unreportedbyContractors", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.contractor IN (:contractors) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)")
@NamedQuery(name = "ReportLine.unreportedbyPositionTypes", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.positionType IN (:positionTypes) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)")
@NamedQuery(name = "ReportLine.unreportedbyContractorsPositionTypes", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.contractor IN (:contractors) AND r.positionType IN (:positionTypes) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)")
@NamedQuery(name = "ReportLine.revenueByPositionTypesAndDateReported", query = "SELECT new eu.ggnet.dwoss.report.ee.eao.RevenueHolder(rl.reportingDate, rl.documentType, rl.salesChannel, rl.contractor, sum(rl.price), sum(rl.purchasePrice))"
            + " FROM ReportLine rl WHERE rl.positionType in(:positions) and rl.reportingDate >= :start and rl.reportingDate <= :end and rl.documentType in(1,3) "
            + " and rl.purchasePrice != 0 GROUP BY rl.reportingDate, rl.documentType, rl.salesChannel, rl.contractor")  // Purchase price is indentifier, that it has been reported.
@NamedQuery(name = "ReportLine.revenueByPositionTypesAndDate", query = "SELECT new eu.ggnet.dwoss.report.ee.eao.RevenueHolder(rl.reportingDate, rl.documentType, rl.salesChannel, rl.contractor, sum(rl.price), 0.)"
            + " FROM ReportLine rl WHERE rl.positionType in(:positions) and rl.reportingDate >= :start"
            + " and rl.reportingDate <= :end and rl.documentType in(1,3) GROUP BY rl.reportingDate, rl.documentType, rl.salesChannel, rl.contractor")
@SuppressWarnings("PersistenceUnitPresent")
public class ReportLine extends BaseEntity implements Serializable, EagerAble, Comparable<ReportLine> {

    /**
     * Types for single line references.
     */
    public static enum SingleReferenceType {

        /**
         * Represents a reference between a Line of type UNIT and WARRANTY in the same Document.
         */
        WARRANTY

    }

    public static class Storeable implements Serializable {

        public final long id;

        public final double marginPercentage;

        public final double purchasePrice;

        public Storeable(long id, double marginPercentage, double purchasePrice) {
            this.id = id;
            this.marginPercentage = marginPercentage;
            this.purchasePrice = purchasePrice;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    /**
     * Helper Class to ensure no StackOverflows on fetchEager.
     */
    static class EagerHelper {

        private final Set<Report> reports = new HashSet<>();

        private final Set<ReportLine> lines = new HashSet<>();

        public void fetch(ReportLine line) {
            if ( lines.contains(line) ) return;
            lines.add(line);
            for (ReportLine reference : line.refrences) {
                fetch(reference);
            }
            for (Report report : line.reports) {
                fetch(report);
            }
        }

        public void fetch(Report report) {
            if ( reports.contains(report) ) return;
            reports.add(report);
            for (ReportLine reportLine : report.getLines()) {
                fetch(reportLine);
            }
        }
    }

    /**
     * The WorkflowStatus represents the status of a ReportedLine.
     * A Status other then Default assumes, that a Document can be reported twice. For now this is the Case for the {@link DocumentType#COMPLAINT}.
     * <ul>
     * <li>UNDER_PROGRESS is a Line if a Complaint is created.</li>
     * <li>CHARGED is a Line if a Complaint has the condition ACCEPTED.</li>
     * <li>DISCHARGED is a Line if a Complaint has one of the conditions WITHDRAWN or REJECTED.</li>
     * </ul>
     * <p>
     */
    public static enum WorkflowStatus {

        /**
         * Default status, no other Line is assume of the same Document.
         */
        DEFAULT(""),
        /**
         * (External) Workflow is in a state, that it will create another line.
         * (e.g. A Complaint is created)
         */
        UNDER_PROGRESS("(o)"),
        /**
         * (External) Workflow is in a end state, that results in some form of continuance.
         * (e.g. Complaint is accepted, so a Credit Memo will be created)
         */
        CHARGED("(+)"),
        /**
         * (External) Workflow is in a end state, that does not contiune.
         * (e.g. Complaint is reject, so nothing happens)
         */
        DISCHARGED("(-)");

        /**
         * A simple sign reprensentation of the Workflow (o), (-) or (+).
         */
        public final String sign;

        private WorkflowStatus(String sign) {
            this.sign = sign;
        }

    }

    public static Set<ReportLine.Storeable> toStorables(Collection<ReportLine> lines) {
        Set<ReportLine.Storeable> result = new HashSet<>();
        for (ReportLine rl : lines) {
            result.add(rl.toStorable());
        }
        return result;
    }

    @Id
    @GeneratedValue
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    private int optLock;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private final Set<ReportLine> refrences = new HashSet<>();

    /**
     * The date when this {@link ReportLine} was reported.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date reportingDate;

    /**
     * The dossier id of the {@link ReportLine} where the referendet position exist.
     */
    @NotNull
    private long dossierId;

    /**
     * The Identifier of the referended Dossier.
     */
    private String dossierIdentifier;

    /**
     * The id of the document where the refended position of this {@link ReportLine} exist.
     */
    @NotNull
    private long documentId;

    /**
     * The Identifier of the referended Document.
     */
    private String documentIdentifier;

    /**
     * The type of the document that referend the {@link ReportLine}.
     */
    @NotNull
    private DocumentType documentType;

    private String documentTypeName;

    /**
     * The date when the newest Document was created.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date actual;

    @NotNull
    private WorkflowStatus workflowStatus = WorkflowStatus.DEFAULT;

    //PositionData
    private double amount;

    /**
     * Name of the RedTape Position.
     */
    @NotNull
    private String name;

    /**
     * The description of the Position.
     */
    @Lob
    @Column(length = 65536)
    private String description;

    /**
     * The type of the position that referend the {@link ReportLine}.
     */
    @NotNull
    private PositionType positionType;

    /**
     * The type of the position that referend the {@link ReportLine}.
     */
    private String positionTypeName;

    /**
     * The price without tax of this position, normally the sales price of a unit.
     */
    private double price;

    private double tax;

    private int bookingAccount;

    /**
     * The id of the Customer wiche has the dossier.
     */
    @Min(0)
    private long customerId;

    /**
     * This String contains a escaped String with the Invoice Adress.
     */
    @Size(max = 255)
    private String customerName;

    @Size(max = 255)
    private String customerCompany;

    @Lob
    @Column(length = 65536)
    private String invoiceAddress;

    // -- Product
    /**
     * This is a referenz to the ProductGroup of the Product, if this ReportLine represent a Unit oder Product.
     */
    private ProductGroup productGroup;

    /**
     * This is a String representation of the ProductGroup.
     * This String exist because the possibility that a Product Group will change his name or will be deleted.
     */
    private String productGroupName;

    /**
     * This is a referenz to the ProductBrand of the Product, if this ReportLine represent a Unit oder Product.
     */
    private TradeName productBrand;

    /**
     * This is a String representation of the Product Brand.
     * This String exist because the possibility that a Product Brand will change his name or will be deleted.
     */
    private String productBrandName;

    /**
     * This is a String representation of the Product Name.
     */
    private String productName;

    /**
     * The part no of the product.
     */
    private String partNo;

    /**
     * This double is the manufacturer cost price.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    private double manufacturerCostPrice;

    /**
     * The id of the product. If the id is 0, than there is no Product.
     */
    private long productId;

    // -- Unit
    /**
     * The refurbish id of the {@link ReportLine}, when it exist.
     */
    private String refurbishId;

    /**
     * The serial number of the unit.
     */
    private String serial;

    /**
     * The manufacture date.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date mfgDate;

    /**
     * The id of the unique unit.
     */
    private long uniqueUnitId;

    /**
     * The percentage of the marge of this Line.
     */
    private double marginPercentage;

    /**
     * The Price with this unit was buyed.
     */
    private double purchasePrice;

    private SalesChannel salesChannel;

    private String salesChannelName;

    /**
     * This is a referenz to the contractor of the Product, if this ReportLine represent a Unit oder Product.
     */
    private TradeName contractor;

    /**
     * This is a String representation of the contractor name.
     * This String exist because the possibility that a contractor name will change his name or will be deleted.
     */
    private String contractorName;

    /**
     * This String is a Partnumber that we will be given from the specific contractor.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    private String contractorPartNo;

    /**
     * This double is the reference price for the specific contractor.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    private double contractorReferencePrice;

    private String customerEmail;

    /**
     * This set contains the ReportsLines of the Reports, where is the mapping unidirectional.
     */
    @ManyToMany(mappedBy = "lines")
    Set<Report> reports = new HashSet<>();

    @NotNull
    @ManyToMany
    @MapKeyEnumerated
    @JoinTable(name = "ReportLine_SingleReferences")
    private final Map<SingleReferenceType, ReportLine> singleReferences = new HashMap<>();

    @Column(length = 65536)
    @Lob
    private String comment;

    private long gtin;

    public ReportLine() {
    }

    /**
     * Offline Constructor, use only in test to simulate ids.
     *
     * @param id the id, normaly autogenerated
     */
    public ReportLine(long id) {
        LoggerFactory.getLogger(this.getClass()).warn("ReportLine(id={}) called, creating intance with id. Use only in tests", id);
        this.id = id;
    }

    public static ReportLineBuilder builder() {
        return new ReportLineBuilder();
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public Date getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(Date reportingDate) {
        this.reportingDate = reportingDate;
    }

    public long getDossierId() {
        return dossierId;
    }

    public void setDossierId(long dossierId) {
        this.dossierId = dossierId;
    }

    public String getDossierIdentifier() {
        return dossierIdentifier;
    }

    public void setDossierIdentifier(String dossierIdentifier) {
        this.dossierIdentifier = dossierIdentifier;
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public Date getActual() {
        return actual;
    }

    public void setActual(Date actual) {
        this.actual = actual;
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getBookingAccount() {
        return bookingAccount;
    }

    public void setBookingAccount(int bookingAccount) {
        this.bookingAccount = bookingAccount;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCompany() {
        return customerCompany;
    }

    public void setCustomerCompany(String customerCompany) {
        this.customerCompany = customerCompany;
    }

    public String getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getRefurbishId() {
        return refurbishId;
    }

    public void setRefurbishId(String refurbishId) {
        this.refurbishId = refurbishId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public long getUniqueUnitId() {
        return uniqueUnitId;
    }

    public void setUniqueUnitId(long uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
    }

    public String getContractorPartNo() {
        return contractorPartNo;
    }

    public void setContractorPartNo(String contractorPartNo) {
        this.contractorPartNo = contractorPartNo;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getGtin() {
        return gtin;
    }

    public void setGtin(long gtin) {
        this.gtin = gtin;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getOptLock() {
        return optLock;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public String getPositionTypeName() {
        return positionTypeName;
    }

    public double getPrice() {
        return price;
    }

    public double getManufacturerCostPrice() {
        return manufacturerCostPrice;
    }

    public double getMarginPercentage() {
        return marginPercentage;
    }

    public TradeName getContractor() {
        return contractor;
    }

    public String getContractorName() {
        return contractorName;
    }

    public double getContractorReferencePrice() {
        return contractorReferencePrice;
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public TradeName getProductBrand() {
        return productBrand;
    }

    public String getProductBrandName() {
        return productBrandName;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public SalesChannel getSalesChannel() {
        return salesChannel;
    }

    public String getSalesChannelName() {
        return salesChannelName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setManufacturerCostPrice(double value) {
        this.manufacturerCostPrice = value;
    }

    public void setMarginPercentage(double value) {
        this.marginPercentage = value;
    }

    public void setPurchasePrice(double value) {
        this.purchasePrice = value;
    }

    public void setContractorReferencePrice(double value) {
        this.contractorReferencePrice = value;
    }

    //</editor-fold>
    public ReportLine getReference(SingleReferenceType type) {
        return singleReferences.get(type);
    }

    public void setReference(SingleReferenceType type, ReportLine line) {
        if ( line == null ) return;
        singleReferences.put(type, line);
        line.singleReferences.put(type, this);
    }

    public double toAfterTaxPrice() {
        return TwoDigits.roundedApply(getPrice(), getTax(), 0.00);
    }

    /**
     * This setter Method sets the productGroup and the productGroupName.
     * <p/>
     * @param productGroup
     */
    public void setProductGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
        if ( productBrand != null ) {
            this.productGroupName = productGroup.getNote();
        } else this.productGroupName = null;
    }

    /** This setter Method sets the productBrand and the productBrandName.
     * <p/>
     * @param productBrand
     */
    public void setProductBrand(TradeName productBrand) {
        this.productBrand = productBrand;
        if ( productBrand != null ) {
            this.productBrandName = productBrand.getDescription();
        } else this.productBrandName = null;
    }

    /**
     * This setter Method sets the contractor and the contractorName.
     * <p/>
     * @param contractor
     */
    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
        if ( contractor != null ) {
            this.contractorName = contractor.getDescription();
        } else this.contractorName = null;
    }

    /**
     * This setter Method sets the salesChannel and the salesChannelName.
     * <p/>
     * @param salesChannel
     */
    public void setSalesChannel(SalesChannel salesChannel) {
        this.salesChannel = salesChannel;
        if ( salesChannel != null ) {
            this.salesChannelName = salesChannel.description;
        } else this.salesChannelName = null;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
        this.documentTypeName = documentType.description;
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
        this.positionTypeName = positionType.description;
    }

    public void addAll(Collection<ReportLine> lines) {
        if ( lines == null || lines.isEmpty() ) return;
        for (ReportLine reportLine : lines) {
            add(reportLine);
        }
    }

    /**
     * Returns all references in a new collection.
     * <p>
     * @return all references in a new collection.
     */
    public NavigableSet<ReportLine> getRefrences() {
        return new TreeSet<>(refrences);
    }

    /**
     * Calls {@link Collection#retainAll(java.util.Collection) } on a new instance of references and returns the resulting collection.
     * <p>
     * @param other the other collection to use as parameter on retainAll.
     * @return the resulting collection
     */
    public NavigableSet<ReportLine> retainReferences(Collection<ReportLine> other) {
        NavigableSet<ReportLine> result = getRefrences();
        result.retainAll(other);
        return result;
    }

    /**
     * This add a ReportLine of the references Set. This is a bidirectional mapping method.
     * <p/>
     * @param reportLine
     */
    public void add(ReportLine reportLine) {
        if ( reportLine == null ) return;
        if ( this.equals(reportLine) ) return; // Don't ref oureself.
        refrences.add(reportLine);
        reportLine.refrences.add(this);
    }

    /**
     * This removes a ReportLine of the references Set. This is a bidirectional mapping method.
     * <p/>
     * @param reportLine
     */
    public void remove(ReportLine reportLine) {
        if ( reportLine == null ) return;
        refrences.remove(reportLine);
        reportLine.refrences.remove(this);
    }

    /**
     * Returs a unmodifiable Set of assosiated reports.
     * <p>
     * @return a unmodifiable Set of assosiated reports.
     */
    public Set<Report> getReports() {
        return Collections.unmodifiableSet(reports);
    }

    /**
     * Returns true if this line is allready in a report of the supplied type, meaning it has been reported.
     * <p>
     * @param reportType the reportType.
     * @return true if this line is allready in a report of the supplied type, meaning it has been reported.
     */
    public boolean isInReport(TradeName reportType) {
        for (Report report : reports) {
            if ( report.getType() == reportType ) return true;
        }
        return false;
    }

    /**
     * Returns the ReportLine matching the type and limited to the same dossier.
     * <p>
     * @param documentTypes one of the types that the reference must have
     * @return the ReportLine matching the type and optional limited to the same dossier or null if no match.
     */
    public ReportLine getSingleReference(DocumentType... documentTypes) {
        return getSingleReference(null, documentTypes);
    }

    /**
     * Returns the ReportLine matching the type and limited to the same dossier.
     * <p>
     * @param positionType  the type the position must have, if null is ignored.
     * @param documentTypes one of the types that the reference must have
     * @return the ReportLine matching the type and optional limited to the same dossier or null if no match.
     */
    public ReportLine getSingleReference(PositionType positionType, DocumentType... documentTypes) {
        // TODO: There is a possibility, that more than one element matches. Not taken into account for now.
        for (ReportLine line : refrences) {
            if ( !Arrays.asList(documentTypes).contains(line.getDocumentType()) ) continue;
            if ( positionType != null && line.getPositionType() != positionType ) continue;
            if ( this.dossierId != line.dossierId ) continue;
            return line;
        }
        return null;
    }

    public boolean hasReferenceTo(PositionType positionType, DocumentType... documentTypes) {
        return getSingleReference(positionType, documentTypes) != null;
    }

    /**
     * Returns true if this line gets discharged by some reference.
     * <p>
     * @return true if this line gets discharged by some reference.
     */
    // TODO: This is incomplete, if we have multipe complaints on one position in the same dossier. Hope this dosn't happen for a while. See http://overload.ahrensburg.gg-net.de/jira/browse/DW-1357
    public boolean isDischarged() {
        for (ReportLine ref : refrences) {
            if ( this.dossierId == ref.dossierId
                    && ref.getDocumentType() == DocumentType.COMPLAINT
                    && ref.getPositionType() == PositionType.UNIT
                    && ref.getWorkflowStatus() == WorkflowStatus.DISCHARGED ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if and only if there are existing repayments of type unit_annex on the same dossier.
     * In the case, than one or more repayment with a unit_annex and a repayment with unit exists, this method will return false.
     * <p>
     * @return true if and only if there are existing repayments of type unit_annex on the same dossier.
     */
    public boolean isPartialRepayed() {
        if ( isFullRepayed() ) return false;
        // A partial repayment exists, so the true
        return getSingleReference(PositionType.UNIT_ANNEX, DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO) != null;
    }

    /**
     * Returns true if and only if there are existing repayments of type unit_annex on the same dossier and in the supplied collection.
     * In the case, than one or more repayment with a unit_annex and a repayment with unit exists, this method will return false.
     * <p>
     * @param other a collection to filter even more, for example by another report, must not be null.
     * @return true if and only if there are existing repayments of type unit_annex on the same dossier and in the supplied collection.
     * @throws NullPointerException if other is null
     */
    public boolean isPartialRepayedIn(Collection<ReportLine> other) throws NullPointerException {
        if ( isFullRepayedIn(other) ) return false;
        // A partial repayment, so the true
        ReportLine repaymentRef = getSingleReference(PositionType.UNIT_ANNEX, DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO);
        if ( repaymentRef == null ) return false;
        return Objects.requireNonNull(other, "Other must not be null").contains(repaymentRef);
    }

    /**
     * Returns true if this line hasNoRepayments.
     * <p>
     * @return true if this line hasNoRepayments.
     */
    public boolean hasNoRepayments() {
        if ( isFullRepayed() ) return false;
        else if ( isPartialRepayed() ) return false;
        return true;
    }

    /**
     * Returns true if this line has no open Complaints, either no complaints at all, or a complaint, that is discharged.
     * <p>
     * @return true if this line has no open Complaints, either no complaints at all, or a complaint, that is discharged.
     */
    public boolean hasNoOpenComplaints() {
        if ( !hasReferenceTo(UNIT, COMPLAINT) ) return true;
        if ( isDischarged() ) return true;
        return false;
    }

    /**
     * Returns ture if there exists a repayment of type unit on the same dossier.
     * <p>
     * @return ture if there exists a repayment of type unit on the same dossier.
     */
    public boolean isFullRepayed() {
        // ShortCut
        if ( refrences.isEmpty() ) return false;
        return getSingleReference(PositionType.UNIT, DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO) != null;
    }

    /**
     * Returns ture if there exists a repayment of type unit on the same dossier and in the supplied collection.
     * <p>
     * @param other a collection to filter down the range, must not be null.
     * @return ture if there exists a repayment of type unit on the same dossier and in the supplied collection.
     * @throws NullPointerException if other is null
     */
    public boolean isFullRepayedIn(Collection<ReportLine> other) throws NullPointerException {
        // ShortCut
        if ( refrences.isEmpty() || other == null || other.isEmpty() ) return false;
        // The ?: needed for the case that a unit_annex is asked if it has been full repaied.
        ReportLine repaymentRef = getSingleReference(getPositionType() == UNIT_ANNEX ? UNIT : getPositionType(),
                DocumentType.ANNULATION_INVOICE, DocumentType.CREDIT_MEMO);
        // No full repayment exists
        if ( repaymentRef == null ) return false;
        return other.contains(repaymentRef);
    }

    /**
     * Convenience Method for documentType is repayment and posisitionType is unit annex.
     * <p>
     * @return ture if documentType is repayment and posisitionType is unit annex.
     */
    public boolean isPartialRepayment() {
        return (documentType == CREDIT_MEMO || documentType == ANNULATION_INVOICE) && positionType == PositionType.UNIT_ANNEX;
    }

    /**
     * Convenience Method for documentType is repayment and posisitionType is unit.
     * <p>
     * @return ture if documentType is repayment and posisitionType is unit.
     */
    public boolean isFullRepayment() {
        return (documentType == CREDIT_MEMO || documentType == ANNULATION_INVOICE) && (positionType == UNIT || positionType == PRODUCT_BATCH);
        // TODO: A much safer way would be to verify that the amount and the price result = 0. But it should be okay for now.
    }

    /**
     * Returns a Set of document types, which are references exist.
     * <p>
     * @return a Set of document types, which are references exist.
     */
    public NavigableSet<DocumentType> getReferenceTypes() {
        NavigableSet<DocumentType> types = new TreeSet<>();
        for (ReportLine ref : refrences) {
            types.add(ref.getDocumentType());
        }
        return types;
    }

    @Override
    public int compareTo(ReportLine o) {
        if ( this.dossierId != o.dossierId ) return Long.valueOf(this.dossierId).compareTo(o.dossierId);
        if ( this.documentId != o.documentId ) return Long.valueOf(this.documentId).compareTo(o.documentId);
        if ( !this.documentType.equals(o.documentType) ) return this.documentType.compareTo(o.documentType);
        // Fallback to equality.
        return Integer.valueOf(this.hashCode()).compareTo(o.hashCode());
    }

    /**
     * Returns a correct name , either using the product or the name.
     * <p/>
     * @return a correct name , either using the product or the name.
     */
    public String toName() {
        if ( StringUtils.isBlank(this.getProductBrandName()) || StringUtils.isBlank(this.getProductName()) ) return this.getName();
        return this.getProductBrandName() + " " + this.getProductName();
    }

    /**
     * Returns a mapping to api.SimpleReportLine.
     *
     * @return a mapping to api.SimpleReportLine.
     */
    public eu.ggnet.dwoss.report.api.SimpleReportLine toSimpleLine() {
        String reportNames = null;
        if ( !getReports().isEmpty() ) reportNames = getReports().stream().map(Report::getName).collect(Collectors.joining(","));
        return new eu.ggnet.dwoss.report.api.SimpleReportLine.Builder()
                .reportingDate(Utils.toLd(reportingDate))
                .actual(Utils.toLd(actual))
                .id(id)
                .refurbishId(refurbishId)
                .positionType(positionType)
                .documentType(documentType)
                .dossierIdentifier(dossierIdentifier)
                .isWarranty(positionType == PRODUCT_BATCH && getReference(WARRANTY) != null)
                .nullableReportName(reportNames)
                .build();
    }

    /**
     * Returns a Storable of this ReportLine.
     * <p>
     * @return a Storable of this ReportLine.
     */
    public Storeable toStorable() {
        return new Storeable(id, marginPercentage, purchasePrice);
    }

    /**
     * Eager Fetch Helper.
     */
    @Override
    public void fetchEager() {
        EagerHelper eagerHelper = new EagerHelper();
        eagerHelper.fetch(this);
    }

    @Override
    public String toString() {
        return "ReportLine{" + "id=" + id + ", reportingDate=" + reportingDate + ", dossierId=" + dossierId + ", dossierIdentifier=" + dossierIdentifier
                + ", documentId=" + documentId + ", documentIdentifier=" + documentIdentifier + ", documentType=" + documentType
                + ", documentTypeName=" + documentTypeName + ", actual=" + actual
                + ", workflowStatus=" + workflowStatus + ", amount=" + amount + ", name=" + name + ", description=" + description
                + ", positionType=" + positionType + ", positionTypeName=" + positionTypeName + ", price=" + price + ", tax=" + tax
                + ", bookingAccount=" + bookingAccount + ", customerId=" + customerId
                + ", invoiceAddress=" + invoiceAddress + ", productGroup=" + productGroup + ", productGroupName=" + productGroupName
                + ", productBrand=" + productBrand + ", productBrandName=" + productBrandName + ", productName=" + productName + ", partNo=" + partNo
                + ", manufacturerCostPrice=" + manufacturerCostPrice + ", productId=" + productId + ", refurbishId=" + refurbishId + ", serial=" + serial
                + ", mfgDate=" + mfgDate + ", uniqueUnitId=" + uniqueUnitId + ", marginPercentage=" + marginPercentage + ", purchasePrice=" + purchasePrice
                + ", salesChannel=" + salesChannel + ", salesChannelName=" + salesChannelName + ", contractor=" + contractor
                + ", contractorName=" + contractorName + ", contractorPartNo=" + contractorPartNo + ", contractorReferencePrice=" + contractorReferencePrice
                + ", gtin=" + gtin + '}';
    }

    /**
     * Returns a simpler String.
     * <p>
     * @return a simpler String.
     */
    public String toSimple() {
        return "ReportLine{" + "id=" + id + ", refurbishId=" + refurbishId + ", reportingDate=" + reportingDate + ", mfgDate=" + mfgDate + '}';
    }

    /**
     * ToString HTML representation.
     *
     * @return HTML view of the ReportLine.
     */
    public String toHtml() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder("");
        sb.append("<h1><b>ReportLine </b></h1>");

        sb.append("<div style='font-family: Sans-Serif; background-color: #e8e8e8;' border='0'>");
        sb.append("<b>id: </b>");
        sb.append(id);
        sb.append("<br>");

        sb.append("<b>ReportingDate: </b>");
        sb.append(reportingDate);
        sb.append("<br>");

        sb.append("<b>DossierId: </b>");
        sb.append(dossierId);
        sb.append("<br>");

        sb.append("<b>DossierIdentifier: </b>");
        sb.append(dossierIdentifier);
        sb.append("<br>");

        sb.append("<b>DocumentId: </b>");
        sb.append(documentId);
        sb.append("<br>");

        sb.append("<b>DocumentIdentifier: </b>");
        sb.append(documentIdentifier);
        sb.append("<br>");

        sb.append("<b>DocumentType: </b>");
        sb.append(documentType.getName());
        sb.append("<br>");

        sb.append("<b>WorkflowStatus: </b>");
        sb.append(workflowStatus.sign);
        sb.append("</div>");

        //postition
        sb.append("<table style='width: 100%; background-color: #e8e8e8; font-family: Sans-Serif;' border='0'><tbody>");
        sb.append("<tr>");

        sb.append("<td valign='top' colspan='2'>");
        sb.append("<font size='4'><b><u>Postition</u></b></font><br>");
        sb.append("<b>Amount: </b>");
        sb.append(amount);
        sb.append("<br>");

        sb.append("<b>Name: </b>");
        sb.append(name);
        sb.append("<br>");

        sb.append("<b>Description: </b><br>");
        sb.append("<textarea  rows='5' cols='70' disabled>");
        sb.append(description);
        sb.append("</textarea>");
        sb.append("<br>");

        sb.append("<b>PositionType: </b>");
        sb.append(positionType.getName());
        sb.append("<br>");

        sb.append("<b>Price (in Euro): </b>");
        sb.append(price);
        sb.append("<br>");

        sb.append("<b>Tax: </b>");
        sb.append(tax);
        sb.append("<br>");

        sb.append("<b>Booking Account: </b>");
        sb.append(bookingAccount);
        sb.append("</td>");
        sb.append("</tr>");

        //product
        sb.append("<tr>");
        sb.append("<td valign='top'>");
        sb.append("<font size='4'><b><u>Product</u></b></font><br>");

        sb.append("<b>ProductId: </b>");
        sb.append(productId);
        sb.append("<br>");

        sb.append("<b>ProductGroup: </b>");
        sb.append(productGroup.getName());
        sb.append("<br>");

        sb.append("<b>ProductBrand: </b>");
        sb.append(productBrand.getName());
        sb.append("<br>");

        sb.append("<b>ProductName: </b>");
        sb.append(productName);
        sb.append("<br>");

        sb.append("<b>PartNo: </b>");
        sb.append(partNo);
        sb.append("<br>");

        sb.append("<b>Manufacturer CostPrice: </b>");
        sb.append(manufacturerCostPrice);
        sb.append("</td>");

        //customer
        sb.append("<td valign='top'>");
        sb.append("<font size='4'><b><u>Customer</u></b></font><br>");
        sb.append("<b>CustomerId: </b>");
        sb.append(customerId);
        sb.append("<br>");

        sb.append("<b>Customer Name: </b>");
        sb.append(customerName);
        sb.append("<br>");

        sb.append("<b>Customer Company: </b>");
        sb.append(customerCompany);
        sb.append("<br>");

        sb.append("<b>Invoice Address: </b><br>");
        sb.append("<textarea  rows='3' cols='40' disabled>");
        sb.append(invoiceAddress);
        sb.append("</textarea>");
        sb.append("</td>");
        sb.append("</tr>");

        //unit
        sb.append("<tr>");
        sb.append("<td valign='top'>");
        sb.append("<font size='4'><b><u>Unit</u></b></font><br>");
        sb.append("<b>RefurbishId: </b>");
        sb.append(refurbishId);
        sb.append("<br>");

        sb.append("<b>Serial: </b>");
        sb.append(serial);
        sb.append("<br>");

        sb.append("<b>mfg Date: </b>");
        if ( mfgDate != null ) sb.append(simpleDateFormat.format(mfgDate));
        sb.append("<br>");

        sb.append("<b>UniqueUnitId: </b>");
        sb.append(uniqueUnitId);
        sb.append("<br>");

        sb.append("<b>Margin Percentage: </b>");
        sb.append(marginPercentage);
        sb.append("<br>");

        sb.append("<b>Purchase Price: </b>");
        sb.append(purchasePrice);
        sb.append("<br>");

        sb.append("<b>SalesChannel: </b>");
        sb.append(salesChannel.getName());

        sb.append("</td>");
        //contractor
        sb.append("<td valign='top'>");
        sb.append("<font size='4'><b><u>Contractor</u></b></font><br>");
        sb.append("<b>Contractor: </b>");
        sb.append(contractor.getName());
        sb.append("<br>");

        sb.append("<b>Contractor PartNo: </b>");
        sb.append(contractorPartNo);
        sb.append("<br>");

        sb.append("<b>Contractor ReferencePrice: </b>");
        sb.append(contractorReferencePrice);
        sb.append("<br>");

        sb.append("<b>GTIN: </b>");
        sb.append(gtin);

        sb.append("</td>");

        sb.append("</tr>");
        sb.append("</tbody></table>");

        return sb.toString();
    }

}
