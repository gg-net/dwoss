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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RedTapeCloserManualOperation implements RedTapeCloserManual {

    @Inject
    private RedTapeCloserAutomaticOperation op;
    
    private final static Logger L = LoggerFactory.getLogger(RedTapeCloserManualOperation.class);

    /**
     * Executes the closing manual.
     * See {@link #closeing(java.lang.String, boolean) } for details.
     * <p>
     * @param arranger the arranger
     */
    @Override
    public void executeManual(String arranger) {
        L.debug("{} called manual closing operation", arranger);
        op.closeing(arranger, true);
    }

    
}
