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
package eu.ggnet.dwoss.report.ee;

import java.io.Serializable;
import java.util.Date;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.Report.ViewMode;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface ReportParameter extends Serializable {

    class Builder extends ReportParameter_Builder {
        
        @SuppressWarnings("OverridableMethodCallInConstructor")
        public Builder () {
            viewMode(ViewMode.DEFAULT); 
        }
        
        @Override
        public Builder reportName(String name) {
            checkArgument(name != null, "Name must not be null");
            checkArgument(name.length() > 1, "Name length must be > 1");
            return super.reportName(name);
        }
        
    };

    TradeName contractor();

    Report.ViewMode viewMode();

    String reportName();

    Date start();

    Date end();

}
