package eu.ggnet.dwoss.report.entity;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.SalesChannel;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.commons.lang3.StringUtils;
import org.metawidget.inspector.annotation.*;

import eu.ggnet.dwoss.util.persistence.EagerAble;
import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

import lombok.*;
import lombok.experimental.Builder;

import static eu.ggnet.dwoss.rules.DocumentType.*;
import static eu.ggnet.dwoss.rules.PositionType.*;

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
@NamedQueries({
    @NamedQuery(name = "ReportLine.allReverse", query = "SELECT r FROM ReportLine r ORDER BY r.reportingDate DESC, r.refurbishId ASC"),
    @NamedQuery(name = "ReportLine.byUniqueUnitId", query = "SELECT r FROM ReportLine r WHERE r.uniqueUnitId = ?1"),
    @NamedQuery(name = "ReportLine.byProductIdMissingContractorPartNo", query = "SELECT r FROM ReportLine r WHERE r.productId = ?1 and r.contractor = ?2 and r.contractorPartNo is null"),
    @NamedQuery(name = "ReportLine.byRefurbishId", query = "SELECT r FROM ReportLine r WHERE r.refurbishId = ?1"),
    @NamedQuery(name = "ReportLine.bySerialAndPositionTypeAndDossierId", query = "SELECT r FROM ReportLine r WHERE r.serial = ?1 AND r.positionType = ?2 AND r.dossierId = ?3"),
    @NamedQuery(name = "ReportLine.lastReported", query = "SELECT MAX(r.reportingDate) FROM ReportLine r"),
    @NamedQuery(name = "ReportLine.betweenDates", query = "SELECT r FROM ReportLine r WHERE r.reportingDate BETWEEN ?1 AND ?2"),
    @NamedQuery(name = "ReportLine.unreported", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)"),
    @NamedQuery(name = "ReportLine.unreportedbyContractors", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.contractor IN (:contractors) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)"),
    @NamedQuery(name = "ReportLine.unreportedbyPositionTypes", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.positionType IN (:positionTypes) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)"),
    @NamedQuery(name = "ReportLine.unreportedbyContractorsPositionTypes", query = "SELECT r FROM ReportLine r WHERE r.reportingDate >= :from AND r.reportingDate <= :till AND r.contractor IN (:contractors) AND r.positionType IN (:positionTypes) AND r.id NOT IN (SELECT pl.id FROM Report p JOIN p.lines pl WHERE p.type = :type)"),
    @NamedQuery(name = "ReportLine.revenueByPositionTypesAndDate", query = "SELECT new eu.ggnet.dwoss.report.eao.RevenueHolder(rl.reportingDate, rl.documentType, sum(rl.price), rl.salesChannel)"
                + " FROM ReportLine rl WHERE rl.positionType in(:positions) and rl.reportingDate >= :start"
                + " and rl.reportingDate <= :end and rl.documentType in(1,3) GROUP BY rl.reportingDate, rl.documentType, rl.salesChannel")

})
@NoArgsConstructor
public class ReportLine extends IdentifiableEntity implements Serializable, EagerAble, Comparable<ReportLine> {

    /**
     * Types for single line references.
     */
    public static enum SingleReferenceType {

        /**
         * Represents a reference between a Line of type UNIT and WARRANTY in the same Document.
         */
        WARRANTY

    }

    @Value
    public static class Storeable implements Serializable {

        private final long id;

        private final double marginPercentage;

        private final double purchasePrice;

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
     *
     */
    @AllArgsConstructor
    @Getter
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
        private final String sign;

    }

    public static Set<ReportLine.Storeable> toStorables(Collection<ReportLine> lines) {
        Set<ReportLine.Storeable> result = new HashSet<>();
        for (ReportLine rl : lines) {
            result.add(rl.toStorable());
        }
        return result;
    }

    @Id
    @Getter
    @GeneratedValue
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    @UiHidden
    private int optLock;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private final Set<ReportLine> refrences = new HashSet<>();

    /**
     * The date when this {@link ReportLine} was reported.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @Getter
    @Setter
    private Date reportingDate;

    /**
     * The dossier id of the {@link ReportLine} where the referendet position exist.
     */
    @NotNull
    @Getter
    @Setter
    private long dossierId;

    /**
     * The Identifier of the referended Dossier.
     */
    @Getter
    @Setter
    private String dossierIdentifier;

    /**
     * The id of the document where the refended position of this {@link ReportLine} exist.
     */
    @NotNull
    @Getter
    @Setter
    private long documentId;

    /**
     * The Identifier of the referended Document.
     */
    @Getter
    @Setter
    private String documentIdentifier;

    /**
     * The type of the document that referend the {@link ReportLine}.
     */
    @NotNull
    @Getter
    private DocumentType documentType;

    @Getter
    @UiHidden
    private String documentTypeName;

    /**
     * The date when the newest Document was created.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @Getter
    @Setter
    private Date actual;

    @Getter
    @Setter
    @NotNull
    private WorkflowStatus workflowStatus = WorkflowStatus.DEFAULT;

    //PositionData
    @Getter
    @Setter
    @UiSection("Position")
    private double amount;

    /**
     * Name of the RedTape Position.
     */
    @NotNull
    @Getter
    @Setter
    private String name;

    /**
     * The description of the Position.
     */
    @Lob
    @Getter
    @Setter
    @UiLarge
    @Column(length = 65536)
    private String description;

    /**
     * The type of the position that referend the {@link ReportLine}.
     */
    @NotNull
    @Getter
    private PositionType positionType;

    /**
     * The type of the position that referend the {@link ReportLine}.
     */
    @Getter
    @UiHidden
    private String positionTypeName;

    /**
     * The price without tax of this position, normally the sales price of a unit.
     */
    @Getter
    private double price;

    @Transient
    private transient DoubleProperty priceProperty;

    @Getter
    @Setter
    private double tax;

    /**
     * The price with tax of this position, normally the sales price of a unit.
     */
    @Getter
    private double afterTaxPrice;

    @Transient
    private transient DoubleProperty afterTaxPriceProperty;

    @Getter
    @Setter
    private int bookingAccount;

    /**
     * The id of the Customer wiche has the dossier.
     */
    @Min(0)
    @Getter
    @Setter
    @UiSection("Customer")
    private long customerId;

    /**
     * This String contains a escaped String with the Invoice Adress.
     */
    @Getter
    @Setter
    @Size(max = 255)
    private String customerName;

    @Getter
    @Setter
    @Size(max = 255)
    private String customerCompany;

    @Lob
    @Getter
    @Setter
    @UiLarge
    @Column(length = 65536)
    private String invoiceAddress;

    // -- Product
    /**
     * This is a referenz to the ProductGroup of the Product, if this ReportLine represent a Unit oder Product.
     */
    @Getter
    @UiSection("Product")
    private ProductGroup productGroup;

    /**
     * This is a String representation of the ProductGroup.
     * This String exist because the possibility that a Product Group will change his name or will be deleted.
     */
    @Getter
    @UiHidden
    private String productGroupName;

    /**
     * This is a referenz to the ProductBrand of the Product, if this ReportLine represent a Unit oder Product.
     */
    @Getter
    private TradeName productBrand;

    /**
     * This is a String representation of the Product Brand.
     * This String exist because the possibility that a Product Brand will change his name or will be deleted.
     */
    @Getter
    @UiHidden
    private String productBrandName;

    /**
     * This is a String representation of the Product Name.
     */
    @Setter
    @Getter
    private String productName;

    /**
     * The part no of the product.
     */
    @Getter
    @Setter
    private String partNo;

    /**
     * This double is the manufacturer cost price.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    @Getter
    private double manufacturerCostPrice;

    @Transient
    private transient DoubleProperty manufacturerCostPriceProperty;

    /**
     * The id of the product. If the id is 0, than there is no Product.
     */
    @Getter
    @Setter
    private long productId;

    // -- Unit
    /**
     * The refurbish id of the {@link ReportLine}, when it exist.
     */
    @Getter
    @Setter
    @UiSection("Unit")
    private String refurbishId;

    /**
     * The serial number of the unit.
     */
    @Getter
    @Setter
    private String serial;

    /**
     * The manufacture date.
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @Getter
    @Setter
    private Date mfgDate;

    /**
     * The id of the unique unit.
     */
    @Getter
    @Setter
    private long uniqueUnitId;

    /**
     * The percentage of the marge of this Line.
     */
    @Getter
    private double marginPercentage;

    @Transient
    private transient DoubleProperty marginPercentageProperty;

    /**
     * The Price with this unit was buyed.
     */
    @Getter
    private double purchasePrice;

    @Transient
    private transient DoubleProperty purchasePriceProperty;

    @Getter
    private SalesChannel salesChannel;

    @Getter
    @UiHidden
    private String salesChannelName;

    /**
     * This is a referenz to the contractor of the Product, if this ReportLine represent a Unit oder Product.
     */
    @Getter
    @UiSection("Contractor")
    private TradeName contractor;

    /**
     * This is a String representation of the contractor name.
     * This String exist because the possibility that a contractor name will change his name or will be deleted.
     */
    @Getter
    @UiHidden
    private String contractorName;

    /**
     * This String is a Partnumber that we will be given from the specific contractor.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    @Getter
    @Setter
    private String contractorPartNo;

    /**
     * This double is the reference price for the specific contractor.
     * It is possible and very common that we have access to this, AFTER we created the Report.
     */
    @Getter
    private double contractorReferencePrice;

    @Transient
    private transient DoubleProperty contractorReferencePriceProperty;

    @Getter
    @Setter
    private String customerEmail;

    /**
     * This set contains the ReportsLines of the Reports, where is the mapping unidirectional.
     */
    @ManyToMany(mappedBy = "lines")
    @UiHidden
    Set<Report> reports = new HashSet<>();

    @NotNull
    @ManyToMany
    @MapKeyEnumerated
    @JoinTable(name = "ReportLine_SingleReferences")
    private final Map<SingleReferenceType, ReportLine> singleReferences = new HashMap<>();

    // Change to Add. This is past.
    @Transient
    private transient BooleanProperty addedToReportProperty;

    @Builder
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ReportLine(String name, String description, long dossierId, String dossierIdentifier, long documentId, String documentIdentifier,
                      PositionType positionType, DocumentType documentType, long customerId, double amount, double tax, double price, double afterTaxPrice,
                      int bookingAccount, String invoiceAddress, String refurbishId, long uniqueUnitId, String serial, Date mfgDate, long productId,
                      String partNo, String customerEmail) {
        this.name = name;
        this.description = description;
        this.dossierId = dossierId;
        this.dossierIdentifier = dossierIdentifier;
        this.documentId = documentId;
        this.documentIdentifier = documentIdentifier;
        this.setPositionType(positionType);
        this.setDocumentType(documentType);
        this.customerId = customerId;
        this.amount = amount;
        this.tax = tax;
        this.price = price;
        this.afterTaxPrice = afterTaxPrice;
        this.bookingAccount = bookingAccount;
        this.invoiceAddress = invoiceAddress;
        this.refurbishId = refurbishId;
        this.uniqueUnitId = uniqueUnitId;
        this.serial = serial;
        this.mfgDate = mfgDate;
        this.productId = productId;
        this.partNo = partNo;
        this.customerEmail = customerEmail;
    }

    public ReportLine getReference(SingleReferenceType type) {
        return singleReferences.get(type);
    }

    public void setReference(SingleReferenceType type, ReportLine line) {
        if ( line == null ) return;
        singleReferences.put(type, line);
        line.singleReferences.put(type, this);
    }

    public void setPrice(double price) {
        if ( priceProperty != null ) priceProperty.set(price);
        else this.price = price;
    }

    public DoubleProperty priceProperty() {
        if ( priceProperty == null ) {
            priceProperty = new SimpleDoubleProperty(price);
            priceProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    price = newValue.doubleValue();
                }
            });
        }
        return priceProperty;
    }

    public void setAfterTaxPrice(double price) {
        if ( afterTaxPriceProperty != null ) afterTaxPriceProperty.set(price);
        else this.afterTaxPrice = price;
    }

    public DoubleProperty afterTaxPriceProperty() {
        if ( afterTaxPriceProperty == null ) {
            afterTaxPriceProperty = new SimpleDoubleProperty(afterTaxPrice);
            afterTaxPriceProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    afterTaxPrice = newValue.doubleValue();
                }
            });
        }
        return afterTaxPriceProperty;
    }

    public void setManufacturerCostPrice(double value) {
        if ( manufacturerCostPriceProperty != null ) manufacturerCostPriceProperty.set(value);
        else this.manufacturerCostPrice = value;
    }

    public DoubleProperty manufacturerCostPriceProperty() {
        if ( manufacturerCostPriceProperty == null ) {
            manufacturerCostPriceProperty = new SimpleDoubleProperty(manufacturerCostPrice);
            manufacturerCostPriceProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    manufacturerCostPrice = newValue.doubleValue();
                }
            });
        }
        return manufacturerCostPriceProperty;
    }

    public void setMarginPercentage(double value) {
        if ( marginPercentageProperty != null ) marginPercentageProperty.set(value);
        else this.marginPercentage = value;
    }

    public DoubleProperty marginPercentageProperty() {
        if ( marginPercentageProperty == null ) {
            marginPercentageProperty = new SimpleDoubleProperty(marginPercentage);
            marginPercentageProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    marginPercentage = newValue.doubleValue();
                }
            });
        }
        return marginPercentageProperty;
    }

    public void setPurchasePrice(double value) {
        if ( purchasePriceProperty != null ) purchasePriceProperty.set(value);
        else this.purchasePrice = value;
    }

    public DoubleProperty purchasePriceProperty() {
        if ( purchasePriceProperty == null ) {
            purchasePriceProperty = new SimpleDoubleProperty(purchasePrice);
            purchasePriceProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    purchasePrice = newValue.doubleValue();
                }
            });
        }
        return purchasePriceProperty;
    }

    public void setContractorReferencePrice(double value) {
        if ( contractorReferencePriceProperty != null ) contractorReferencePriceProperty.set(value);
        else this.contractorReferencePrice = value;
    }

    public DoubleProperty contractorReferencePriceProperty() {
        if ( contractorReferencePriceProperty == null ) {
            contractorReferencePriceProperty = new SimpleDoubleProperty(contractorReferencePrice);
            contractorReferencePriceProperty.addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    contractorReferencePrice = newValue.doubleValue();
                }
            });
        }
        return contractorReferencePriceProperty;
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
            this.productBrandName = productBrand.getName();
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
            this.contractorName = contractor.getName();
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
            this.salesChannelName = salesChannel.getName();
        } else this.salesChannelName = null;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
        this.documentTypeName = documentType.getName();
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
        this.positionTypeName = positionType.getName();
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
        if ( isPartialRepayed() ) return false;
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

    public void setAddedToReport(boolean report) {
        if ( addedToReportProperty == null ) addedToReportProperty = new SimpleBooleanProperty(report);
        else addedToReportProperty.set(report);
    }

    public BooleanProperty addedToReportProperty() {
        return addedToReportProperty;
    }

    public boolean isAddedToReport() {
        return addedToReportProperty == null ? false : addedToReportProperty.get();
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
                + ", afterTaxPrice=" + afterTaxPrice + ", bookingAccount=" + bookingAccount + ", customerId=" + customerId
                + ", invoiceAddress=" + invoiceAddress + ", productGroup=" + productGroup + ", productGroupName=" + productGroupName
                + ", productBrand=" + productBrand + ", productBrandName=" + productBrandName + ", productName=" + productName + ", partNo=" + partNo
                + ", manufacturerCostPrice=" + manufacturerCostPrice + ", productId=" + productId + ", refurbishId=" + refurbishId + ", serial=" + serial
                + ", mfgDate=" + mfgDate + ", uniqueUnitId=" + uniqueUnitId + ", marginPercentage=" + marginPercentage + ", purchasePrice=" + purchasePrice
                + ", salesChannel=" + salesChannel + ", salesChannelName=" + salesChannelName + ", contractor=" + contractor
                + ", contractorName=" + contractorName + ", contractorPartNo=" + contractorPartNo + ", contractorReferencePrice=" + contractorReferencePrice + '}';
    }

    /**
     * Returns a simpler String.
     * <p>
     * @return a simpler String.
     */
    public String toSimple() {
        return "ReportLine{" + "id=" + id + ", refurbishId=" + refurbishId + ", reportingDate=" + reportingDate + ", mfgDate=" + mfgDate + '}';
    }

}
