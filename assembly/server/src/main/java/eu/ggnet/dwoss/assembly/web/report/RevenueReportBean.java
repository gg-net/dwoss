package eu.ggnet.dwoss.assembly.web.report;

import eu.ggnet.dwoss.rules.PositionType;

import java.io.Serializable;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.*;

import org.slf4j.*;

import eu.ggnet.dwoss.report.RevenueReportSum;
import eu.ggnet.dwoss.report.op.RevenueReporter;

import lombok.*;

/**
 * This class provides methods for RevenueReport generation in DW-Web.
 * <p>
 * @author pascal.perau
 */
@Named
@SessionScoped
public class RevenueReportBean implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(RevenueReportBean.class);

    @Inject
    private RevenueReporter revenueOperation;

    @Getter
    private List<RevenueReportSum> reportData = new ArrayList<>();

    @Getter
    @Setter
    private Date start;

    @Getter
    @Setter
    private Date end;

    @Getter
    @Setter
    private List<PositionType> choosenPositionTypes = Arrays.asList(PositionType.UNIT);

    @Getter
    private final List<PositionType> positionTypes = Arrays.asList(PositionType.values());

    public boolean isReportDataSet() {
        return (!positionTypes.isEmpty() && start != null && end != null);
    }

    public void findReportData() {
        if ( choosenPositionTypes.isEmpty() )
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Daten fehlen:", "Mindestend ein Positionstyp muss gewählt werden."));

        reportData = new ArrayList<>(revenueOperation.aggregateDailyRevenue(choosenPositionTypes, start, end));
        Collections.sort(reportData);
    }

}
