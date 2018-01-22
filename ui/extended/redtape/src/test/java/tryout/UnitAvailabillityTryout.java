/*
 * Copyright (C) 2017 GG-Net GmbH
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
package tryout;

import java.util.*;

import javax.swing.JLabel;

import eu.ggnet.dwoss.redtape.UnitOverseer;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtapext.ui.cap.UnitAvailabilityViewCask;
import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.dwoss.util.Tuple2;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.interactiveresult.Result;
import eu.ggnet.saft.*;

/**
 *
 * @author oliver.guenther
 */
public class UnitAvailabillityTryout {

    public static void main(String[] args) {
        Client.addSampleStub(UnitOverseer.class, new UnitOverseer() {
            private Map<String, Tuple2<UnitShard, String>> data = new HashMap<>();

            {
                data.put("1", new Tuple2<>(new UnitShard("1", 1, "SopoNr.: 1", Boolean.TRUE, 1), "Details zu Unit 1"));
                data.put("2", new Tuple2<>(new UnitShard("2", 2, "SopoNr.: 2", Boolean.FALSE, 1), "Details zu Unit 2"));
                data.put("3", new Tuple2<>(new UnitShard("3", 3, "SopoNr.: 3", Boolean.TRUE, 1), "Details zu Unit 3"));
                data.put("4", new Tuple2<>(new UnitShard("4", 4, "SopoNr.: 4", Boolean.FALSE, 1), "Details zu Unit 4"));
                data.put("5", new Tuple2<>(new UnitShard("5", 5, "SopoNr.: 5 exitiert nicht", null, null), "Existiert Nicht"));
                data.put("6", new Tuple2<>(new UnitShard("6", 6, "SopoNr.: 6", Boolean.FALSE, null), "Details zu Unit 6"));
            }

            @Override
            public String toDetailedHtml(String refurbishId, String username) {
                if ( !data.containsKey(refurbishId) ) return refurbishId + " existiert nicht";
                return data.get(refurbishId)._2;
            }

            @Override
            public UnitShard find(String refurbishId) {
                if ( !data.containsKey(refurbishId) ) return new UnitShard(refurbishId, 0, "SopoNr.: " + refurbishId + " exitiert nicht", null, null);
                return data.get(refurbishId)._1;
            }

            @Override
            public boolean isAvailable(String refurbishId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void lockStockUnit(long dossierId, String refurbishedId) throws IllegalStateException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Result<List<Position>> createUnitPosition(String refurbishId, long documentId, double tax) throws UserInfoException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String toDetailedHtml(int uniqueUnitId, String username) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        UiCore.startSwing(() -> new JLabel("Main Application"));

        Ui.swing().show(() -> new UnitAvailabilityViewCask());
    }
}
