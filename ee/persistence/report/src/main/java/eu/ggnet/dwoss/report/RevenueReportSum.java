package eu.ggnet.dwoss.report;

import java.io.Serializable;
import java.util.Date;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.*;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.SalesChannel.CUSTOMER;
import static eu.ggnet.dwoss.rules.SalesChannel.RETAILER;

/**
 *
 * @author pascal.perau
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportSum implements Serializable, Comparable<RevenueReportSum> {

    private Date reportingDate;

    private double invoiceSum;

    private double annulationSum;

    private double sumUpCustomer;

    private double sumUpDealer;

    private double sumUpUnknown;

    /**
     * Add an amount to the sum based on a given {@link DocumentType}.
     * <p>
     * Supported {@link DocumentType}<code>s</code> are:
     * <ul>
     * <li>{@link DocumentType#INVOICE}</li>
     * <li>{@link DocumentType#ANNULATION_INVOICE}</li>
     * </ul>
     * <p>
     * @param type   the {@link DocumentType}
     * @param amount the amount to be added.
     */
    public void addSumByDocumentType(DocumentType type, double amount) {
        if ( type == INVOICE ) invoiceSum += amount;
        else if ( type == ANNULATION_INVOICE ) annulationSum += amount;
        else;
    }

    /**
     * Add an amount to the sum based on a given {@link SalesChannel}.
     * <p>
     * Supported {@link DocumentType}<code>s</code> are:
     * <ul>
     * <li>{@link SalesChannel#CUSTOMER}</li>
     * <li>{@link SalesChannel#RETAILER}</li>
     * <li>{@link SalesChannel#UNKNOWN}</li>
     * </ul>
     * <p>
     * @param channel the {@link SalesChannel}
     * @param amount  the amount to be added.
     */
    public void addSalesChannelSum(SalesChannel channel, double amount) {
        if ( channel == CUSTOMER ) sumUpCustomer += amount;
        else if ( channel == RETAILER ) sumUpDealer += amount;
        else sumUpUnknown += amount;
    }

    @Override
    public int compareTo(RevenueReportSum t) {
        return this.reportingDate.compareTo(t.getReportingDate());
    }

}
