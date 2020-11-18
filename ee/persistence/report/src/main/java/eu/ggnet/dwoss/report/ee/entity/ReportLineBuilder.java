/*
 * Copyright (C) 2019 GG-Net GmbH
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

import java.util.Date;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;

/**
 * Unsafe Builder, was the best way in the removal of lombok to do it this way.
 * No null or other validation is done.
 *
 * @author oliver.guenther
 */
public class ReportLineBuilder {
    
    private long id = 0;

    private String name = null;

    private String description = null;

    private long dossierId = 0;

    private String dossierIdentifier = null;

    private long documentId = 0;

    private String documentIdentifier = null;

    private PositionType positionType = null;

    private DocumentType documentType = null;

    private long customerId = 0;

    private double amount = 0;

    private double tax = 0;

    private double price = 0;

    private int bookingAccount = 0;

    private String invoiceAddress = null;

    private String refurbishId = null;

    private long uniqueUnitId;

    private String serial = null;

    private Date mfgDate = null;

    private long productId = 0;

    private String partNo = null;

    private String customerEmail = null;

    public ReportLineBuilder() {
    }
    
    public ReportLineBuilder id(long id) {
        this.id = id;
        return this;
    }

    public ReportLineBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ReportLineBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ReportLineBuilder dossierId(long dossierId) {
        this.dossierId = dossierId;
        return this;
    }

    public ReportLineBuilder dossierIdentifier(String dossierIdentifier) {
        this.dossierIdentifier = dossierIdentifier;
        return this;
    }

    public ReportLineBuilder documentId(long documentId) {
        this.documentId = documentId;
        return this;
    }

    public ReportLineBuilder documentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
        return this;
    }

    public ReportLineBuilder positionType(PositionType positionType) {
        this.positionType = positionType;
        return this;
    }

    public ReportLineBuilder documentType(DocumentType documentType) {
        this.documentType = documentType;
        return this;
    }

    public ReportLineBuilder customerId(long customerId) {
        this.customerId = customerId;
        return this;
    }

    public ReportLineBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public ReportLineBuilder tax(double tax) {
        this.tax = tax;
        return this;
    }

    public ReportLineBuilder price(double price) {
        this.price = price;
        return this;
    }

    public ReportLineBuilder bookingAccount(int bookingAccount) {
        this.bookingAccount = bookingAccount;
        return this;
    }

    public ReportLineBuilder invoiceAddress(String invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
        return this;
    }

    public ReportLineBuilder refurbishId(String refurbishId) {
        this.refurbishId = refurbishId;
        return this;
    }

    public ReportLineBuilder uniqueUnitId(long uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
        return this;
    }

    public ReportLineBuilder serial(String serial) {
        this.serial = serial;
        return this;
    }

    public ReportLineBuilder mfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
        return this;
    }

    public ReportLineBuilder productId(long productId) {
        this.productId = productId;
        return this;
    }

    public ReportLineBuilder partNo(String partNo) {
        this.partNo = partNo;
        return this;
    }

    public ReportLineBuilder customerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
        return this;
    }
    
    public ReportLine build() {
        ReportLine l = (id == 0 ? new ReportLine(): new ReportLine(id));
        l.setName(name);
        l.setDescription(description);
        l.setDossierId(dossierId);
        l.setDossierIdentifier(dossierIdentifier);
        l.setDocumentId(documentId);
        l.setDocumentIdentifier(documentIdentifier);
        l.setPositionType(positionType);
        l.setDocumentType(documentType);
        l.setCustomerId(customerId);
        l.setAmount(amount);
        l.setTax(tax);
        l.setPrice(price);
        l.setBookingAccount(bookingAccount);
        l.setInvoiceAddress(invoiceAddress);
        l.setRefurbishId(refurbishId);
        l.setUniqueUnitId(uniqueUnitId);
        l.setSerial(serial);
        l.setMfgDate(mfgDate);
        l.setProductId(productId);
        l.setPartNo(partNo);
        l.setCustomerEmail(customerEmail);
        return l;
    }

    public static ReportLine create(String name, String description, long dossierId, String dossierIdentifier, long documentId, String documentIdentifier,
                      PositionType positionType, DocumentType documentType, long customerId, double amount, double tax, double price,
                      int bookingAccount, String invoiceAddress, String refurbishId, long uniqueUnitId, String serial, Date mfgDate, long productId,
                      String partNo, String customerEmail) {
        return create(0, name, description, dossierId, dossierIdentifier, documentId, documentIdentifier, positionType, documentType, customerId, amount, tax, 
                price, bookingAccount, invoiceAddress, refurbishId, uniqueUnitId, serial, mfgDate, productId, partNo, customerEmail);
        
    }    
    public static ReportLine create(long id, String name, String description, long dossierId, String dossierIdentifier, long documentId, String documentIdentifier,
                      PositionType positionType, DocumentType documentType, long customerId, double amount, double tax, double price,
                      int bookingAccount, String invoiceAddress, String refurbishId, long uniqueUnitId, String serial, Date mfgDate, long productId,
                      String partNo, String customerEmail) {
        ReportLine l = (id == 0 ? new ReportLine(): new ReportLine(id));
        l.setName(name);
        l.setDescription(description);
        l.setDossierId(dossierId);
        l.setDossierIdentifier(dossierIdentifier);
        l.setDocumentId(documentId);
        l.setDocumentIdentifier(documentIdentifier);
        l.setPositionType(positionType);
        l.setDocumentType(documentType);
        l.setCustomerId(customerId);
        l.setAmount(amount);
        l.setTax(tax);
        l.setPrice(price);
        l.setBookingAccount(bookingAccount);
        l.setInvoiceAddress(invoiceAddress);
        l.setRefurbishId(refurbishId);
        l.setUniqueUnitId(uniqueUnitId);
        l.setSerial(serial);
        l.setMfgDate(mfgDate);
        l.setProductId(productId);
        l.setPartNo(partNo);
        l.setCustomerEmail(customerEmail);
        return l;
    }

}
