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
package eu.ggnet.dwoss.uniqueunit.ee.op;

import java.util.Date;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.system.util.Step;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.common.FileJacket;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface UniqueUnitReporter {

    // TODO: Document Me
    FileJacket quality(Date start, Date end, TradeName contractor);

    FileJacket unitInputAsXls(Date start, Date end, Step step);
}
