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
package eu.ggnet.dwoss.common.api.values.partno;

import java.io.Serializable;

/**
 * A support interface for PartNos.
 * <p>
 * @author oliver.guenther
 */
public interface PartNoSupport extends Serializable {

    /**
     * Returns true if a partNo is valid.
     * <p>
     * @param partNo the part no
     * @return true if a partNo is valid.
     */
    boolean isValid(String partNo);

    /**
     * Returns a string representation of the violation of the partNo or null if the partNo is valid.
     * <p>
     * @param partNo the partNo
     * @return a string representation of the violation of the partNo or null if the partNo is valid.
     */
    String violationMessages(String partNo);

}