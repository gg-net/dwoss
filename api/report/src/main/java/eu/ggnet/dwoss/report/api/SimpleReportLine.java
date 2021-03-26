/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.report.api;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;

/**
 * A line with reporting informationen.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface SimpleReportLine extends Serializable {

    class Builder extends SimpleReportLine_Builder {
    }

    /*
                            l.getId(),
                        Utils.ISO_DATE.format(l.getReportingDate()),
                        l.getCustomerId(),
                        l.getRefurbishId(),
                        l.getPositionType() == PRODUCT_BATCH && l.getReference(WARRANTY) != null ? "Garantieerweiterung" : l.getPositionType().description,
                        l.getDossierIdentifier() + ", " + l.getDocumentType().description + l.getWorkflowStatus().sign + (l.getDocumentIdentifier() == null ? "" : ", " + l.getDocumentIdentifier()),
                        l.getReports().stream().map(Report::getName).collect(Collectors.joining(","))

     */
    /**
     * The date when this {@link ReportLine} was reported.
     *
     * @return the date when this {@link ReportLine} was reported.
     */
    LocalDate reportingDate();

    /**
     * The date when the newest Document was created.
     *
     * @return the date when the newest Document was created.
     */
    LocalDate actual();

    /**
     * Returns database id.
     *
     * @return database id.
     */
    long id();

    /**
     * Return the refurbishid on time of report.
     *
     * @return the refurbishid on time of report.
     */
    String refurbishId();

    /**
     * The position type.
     *
     * @return position type.
     */
    PositionType positionType();

    /**
     * Return true if the line is a warrantyline.
     *
     * @return true if the line is a warrantyline.
     */
    boolean isWarranty();

    /**
     * Return the documentype.
     *
     * @return the documentype.
     */
    DocumentType documentType();

    /**
     * The dossier identifiert on report time
     *
     * @return the dossier identifiert on report time
     */
    String dossierIdentifier();

    /**
     * An Optional report, if the line was reported in a salesreport.
     *
     * @return an optional sales report name. Theroeticly a collection of names.
     */
    Optional<String> reportName();

}
