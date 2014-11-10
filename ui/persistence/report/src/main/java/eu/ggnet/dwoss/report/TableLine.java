package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.report.entity.ReportLine;

import javafx.beans.property.*;

import lombok.*;

@ToString
@EqualsAndHashCode
public class TableLine {

    @Getter
    @Setter
    private ReportLine reportLine;

    private final BooleanProperty shouldReportedProperty = new SimpleBooleanProperty(true);

    public TableLine(ReportLine reportLine) {
        this.reportLine = reportLine;
    }

    public double getCpPercentage() {
        if ( reportLine.getContractorReferencePrice() == 0 ) return 0;
        return reportLine.getPrice() / reportLine.getContractorReferencePrice();
    }

    public double getMargin() {
        return reportLine.getPrice() - reportLine.getPurchasePrice();
    }

    public BooleanProperty shouldReportedProperty() {
        return shouldReportedProperty;
    }

    public void setShouldReported(boolean report) {
        shouldReportedProperty.set(report);
    }

    public boolean isShouldReported() {
        return shouldReportedProperty.get();
    }
}
