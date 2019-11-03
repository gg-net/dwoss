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

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
    @GeneratedValue
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    private int optLock;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date reportingDate;

    private String refurbishId;

    private long uniqueUnitId;

    private TradeName contractor;

    private String partNo;

    private String serial;

    private String productName;

    private double amount;

    private double price;

    @Transient
    private transient DoubleProperty priceProperty;

    private double purchasePrice;

    @Transient
    private transient DoubleProperty purchasePriceProperty;

    private double contractorReferencePrice;

    @Transient
    private transient DoubleProperty contractorReferencePriceProperty;

    @NotNull
    private DocumentType documentType;

    @NotNull
    private PositionType positionType;

    private String comment;

    @NotNull
    private WorkflowStatus workflowStatus = WorkflowStatus.DEFAULT;
    
    public SimpleReportLine() {
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public void setReportingDate(Date reportingDate) {
        this.reportingDate = reportingDate;
    }
    
    public void setRefurbishId(String refurbishId) {
        this.refurbishId = refurbishId;
    }
    
    public void setUniqueUnitId(long uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
    }
    
    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
    }
    
    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    
    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
