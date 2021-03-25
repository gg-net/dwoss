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
package tryout.stub;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitApiStub implements UniqueUnitApi {

    @Override
    public String findBySerialAsHtml(String serial, String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String findAsHtml(long id, String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addHistory(long uniqueUnitId, String history, String arranger) throws UserInfoException {
        if ( history == null || history.isBlank() ) throw new UserInfoException("history darf nicht null oder leer sein");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger dar nicht null oder leer sein");
        if ( uniqueUnitId < 1 || uniqueUnitId > 100000 )
            throw new UserInfoException("UniqueUnitId " + uniqueUnitId + " muß für Stub zwischen 1 und 100000 liegen");
        System.out.println("Speichere History " + history + " - " + arranger + " für Unit: " + uniqueUnitId);
    }

    @Override
    public void addHistoryByRefurbishId(String refurbishId, String history, String arranger) throws UserInfoException {
        if ( history == null || history.isBlank() ) throw new UserInfoException("history darf nicht null oder leer sein");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger dar nicht null oder leer sein");
        if ( refurbishId == null || refurbishId.isBlank() ) throw new UserInfoException("refurbishId darf nicht null oder leer sein");
        try {
            int intId = Integer.parseInt(refurbishId);
            if ( intId < 1 || intId > 100000 ) throw new UserInfoException("Refurbishid " + refurbishId + " must für Stub zwischen 1 und 100000 liegen");
            System.out.println("Speichere History " + history + " - " + arranger + " für Unit: " + refurbishId);
        } catch (NumberFormatException e) {
            throw new UserInfoException("Refurbishid " + refurbishId + " ist keine Zahl");
        }
    }

    @Override
    public FileJacket toUnitsOfPartNoAsXls(String partNo) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
