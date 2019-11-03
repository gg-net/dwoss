/*
 * Copyright (C) 2014 bastian.venz
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
package eu.ggnet.dwoss.misc.ee;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 *
 * @author bastian.venz
 */
@Remote
public interface ResolveRepayment {

    // TODO: Another great class of olli. Please become an imutable
    public static class ResolveResult implements Serializable {

        public String stockMessage;

        public String redTapeMessage;

        public String reportMessage;
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    List<ReportLine> getRepaymentLines(TradeName contractor);

    ResolveResult resolveUnit(String identifier, TradeName contractor, String arranger, String comment) throws UserInfoException;

}
