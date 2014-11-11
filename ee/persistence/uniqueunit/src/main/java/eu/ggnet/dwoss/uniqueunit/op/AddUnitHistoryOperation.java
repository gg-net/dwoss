/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.uniqueunit.op;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.event.UnitHistory;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnitHistory;

/**
 * Implementation of the {@link AddUnitHistory} {@link Remote} interface.
 * It will be used to add {@link UnitHistory} to a {@link UniqueUnit}.
 *
 * @author bastian.venz
 */
@Stateless
public class AddUnitHistoryOperation implements AddUnitHistory {

    private final static Logger L = LoggerFactory.getLogger(AddUnitHistoryOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager entityManager;

    /**
     * This method is used to add a Comment History to a UniqueUnit.
     * @param refurbishId the refurbish id
     * @param comment the comment that will be added
     * @param arranger the arranger
     */
    @Override
    public void addCommentHistory(String refurbishId, String comment, String arranger) {
        UniqueUnitEao eao = new UniqueUnitEao(entityManager);
        UniqueUnit uu = eao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uu != null ) uu.addHistory(UniqueUnitHistory.Type.UNDEFINED, comment + " - " + arranger);
        else L.warn("No UniqueUnit for refurbishId " + refurbishId);
    }

}
