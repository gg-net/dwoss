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
package eu.ggnet.dwoss.report.ee.entity.partial;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.WorkflowStatus;

/**
 *
 * @author oliver.guenther
 */
@Entity
@Table(name = "ReportLine")
@SuppressWarnings("PersistenceUnitPresent")
public class SimpleReportLine extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    private int optLock;

    @Temporal(jakarta.persistence.TemporalType.DATE)
    private Date reportingDate;

    private String refurbishId;

    private long uniqueUnitId;

    private TradeName contractor;

    private String partNo;

    private String serial;

    private String productName;

    private double amount;

    private double price;

    private double purchasePrice;

    private double contractorReferencePrice;

    @NotNull
    private DocumentType documentType;

    @NotNull
    private PositionType positionType;

    private String comment;

    @NotNull
    private WorkflowStatus workflowStatus = WorkflowStatus.DEFAULT;

    public SimpleReportLine() {
    }

    public SimpleReportLine(double amount, TradeName contractor, double contractorReferencePrice, DocumentType documentType, PositionType positionType, double price,
                            String productName, double purchasePrice, String refurbishId, String partNo, long uniqueUnitId, Date reportingDate) {
        this.reportingDate = reportingDate;
        this.refurbishId = refurbishId;
        this.uniqueUnitId = uniqueUnitId;
        this.contractor = contractor;
        this.partNo = partNo;
        this.productName = productName;
        this.amount = amount;
        this.price = price;
        this.purchasePrice = purchasePrice;
        this.contractorReferencePrice = contractorReferencePrice;
        this.documentType = documentType;
        this.positionType = positionType;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public long getId() {
        return id;
    }

    public int getOptLock() {
        return optLock;
    }

    public Date getReportingDate() {
        return reportingDate;
    }

    public String getRefurbishId() {
        return refurbishId;
    }

    public long getUniqueUnitId() {
        return uniqueUnitId;
    }

    public TradeName getContractor() {
        return contractor;
    }

    public String getPartNo() {
        return partNo;
    }

    public String getSerial() {
        return serial;
    }

    public String getProductName() {
        return productName;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getContractorReferencePrice() {
        return contractorReferencePrice;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public String getComment() {
        return comment;
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
