package eu.ggnet.dwoss.report.eao;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Value holder for revenue reporting.
 * <p>
 * @author pascal.perau
 */
@Data
@AllArgsConstructor
public class DailyRevenue {

    private Date reportingDate;

    private String documentTypeName;

    private double dailySum;

    private String salesChannelName;

}
