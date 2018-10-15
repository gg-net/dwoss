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

import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.api.values.TradeName;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.report.ee.entity.ReportLine.WorkflowStatus;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import lombok.*;
import lombok.Builder;

/**
 *
 * @author oliver.guenther
 */
@Entity
@Table(name = "ReportLine")
@ToString
@EqualsAndHashCode(of = "id")
public class SimpleReportLine implements Serializable {

    public SimpleReportLine() {
    }

    @Builder
    public SimpleReportLine(Date reportingDate, String refurbishId, long uniqueUnitId, TradeName contractor, String partNo, String productName, double amount,
                            double price, double purchasePrice, double contractorReferencePrice, DocumentType documentType, PositionType positionType, String serial) {
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

    @Id
    @Getter
    @GeneratedValue
    private long id;

    /**
     * Integer value for optimistic locking.
     */
    @Version
    @Getter
    private int optLock;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Getter
    @Setter
    private Date reportingDate;

    @Getter
    @Setter
    private String refurbishId;

    @Getter
    @Setter
    private long uniqueUnitId;

    @Getter
    @Setter
    private TradeName contractor;

    @Getter
    @Setter
    private String partNo;

    @Getter
    @Setter
    private String serial;

    @Setter
    @Getter
    private String productName;

    @Getter
    @Setter
    private double amount;

    @Getter
    @Setter
    private double price;

    @Transient
    private transient DoubleProperty priceProperty;

    @Getter
    @Setter
    private double purchasePrice;

    @Transient
    private transient DoubleProperty purchasePriceProperty;

    @Getter
    @Setter
    private double contractorReferencePrice;

    @Transient
    private transient DoubleProperty contractorReferencePriceProperty;

    @NotNull
    @Getter
    private DocumentType documentType;

    @NotNull
    @Getter
    @Setter
    private PositionType positionType;

    @Getter
    @Setter
    private String comment;

    @Getter
    @Setter
    @NotNull
    private WorkflowStatus workflowStatus = WorkflowStatus.DEFAULT;

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

}
