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
package eu.ggnet.dwoss.assembly.web.report;

import java.io.Serializable;
import java.util.*;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.PositionType;

import lombok.Getter;
import lombok.Setter;

/**
 * This class provides methods for RevenueReport generation in DW-Web.
 * <p>
 * @author pascal.perau
 */
@Named
@SessionScoped
public class RevenueReportBean implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(RevenueReportBean.class);

//    @Inject
//    private RevenueReporter revenueOperation;
//
//    @Getter
//    private List<RevenueReportSum> reportData = new ArrayList<>();
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

//        reportData = new ArrayList<>(revenueOperation.aggregateDailyRevenue(choosenPositionTypes, start, end));
//        Collections.sort(reportData);
    }

}
