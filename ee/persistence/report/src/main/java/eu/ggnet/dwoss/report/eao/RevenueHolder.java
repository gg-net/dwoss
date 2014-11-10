package eu.ggnet.dwoss.report.eao;

import java.util.Date;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Value holder for revenue reporting.
 * Needed her to be use in a named Query.
 * <p>
 * @author oliver.guenther
 */
@Data
@AllArgsConstructor
public class RevenueHolder {

    private Date reportingDate;

    private DocumentType documentType;

    private double sum;

    private SalesChannel salesChannel;

}
