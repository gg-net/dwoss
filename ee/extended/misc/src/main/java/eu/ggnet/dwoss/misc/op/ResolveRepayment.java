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
package eu.ggnet.dwoss.misc.op;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author bastian.venz
 */
@Local
@Remote
public interface ResolveRepayment {

    List<SimpleReportLine> getRepaymentLines(TradeName contractor);

    void resolveSopo(String identifier, TradeName contractor, String arranger) throws UserInfoException;

}
