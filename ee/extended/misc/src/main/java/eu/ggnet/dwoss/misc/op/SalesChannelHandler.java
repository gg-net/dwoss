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
package eu.ggnet.dwoss.misc.op;

import java.util.List;

import javax.ejb.Remote;

import eu.ggnet.dwoss.stock.model.SalesChannelLine;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface SalesChannelHandler {

    /**
     * Returns all units, which are in a stock. Units which are on a transaction, are not displayed.
     * <p/>
     * @return all units, which are in a stock
     */
    List<SalesChannelLine> findAvailableUnits();

    /**
     * Updates the salesChanel of all supplied units
     * <p/>
     * @param lines              a list of salesChannelLines, must not be null.
     * @param arranger
     * @param transactionComment
     * @return true if something was changed.
     * @throws de.dw.util.UserInfoException
     */
    boolean update(final List<SalesChannelLine> lines, String arranger, String transactionComment) throws UserInfoException;
}
