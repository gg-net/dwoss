/*
 * Copyright (C) 2014 GG-Net GmbH
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
package tryout.search;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oliver.guenther
 */
public class VirtualDataSource {

    private final static Map<Long, Dossier> DOSSIERS = new HashMap<>();

    private final static Map<Integer, Unit> UNITS = new HashMap<>();

    private final static List<SearchResult> SEARCH = Arrays.asList(
            new MicroUnit(1, "Das ist Gerät 1"),
            new MicroUnit(2, "Das ist Gerät 2"),
            new MicroUnit(3, "Das ist Gerät 3"),
            new MicroDossier(1, "Das ist Dossier 1"),
            new MicroDossier(2, "Das ist Dossier 2"),
            new MicroUnit(4, "Das ist Gerät 4"),
            new MicroDossier(3, "Das ist Dossier 3"),
            new MicroUnitDossier(1, 1, "A Mix of Unit 1 and Dossier 1"),
            new MicroReport(1, "Report 1"),
            new MicroReport(2, "Report 2")
    );

    static {
        DOSSIERS.put(1l, new Dossier("Dossier 1", "Eine lange Beschreibung zu Dossier 1"));
        DOSSIERS.put(2l, new Dossier("Dossier 2", "Eine lange Beschreibung zu Dossier 2"));
        DOSSIERS.put(3l, new Dossier("Dossier 3", "Eine lange Beschreibung zu Dossier 3"));

        UNITS.put(1, new Unit("Unit 1", "Viele Details über Unit 1"));
        UNITS.put(2, new Unit("Unit 2", "Viele Details über Unit 2"));
        UNITS.put(3, new Unit("Unit 3", "Viele Details über Unit 3"));
        UNITS.put(4, new Unit("Unit 4", "Viele Details über Unit 4"));
    }

    /**
     * Returs simulated searchs. The double is a relevance value.
     * <p>
     * @return data
     */
    public static List<SearchResult> search() {
        return SEARCH;
    }

    public static Unit findUnit(int id) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(VirtualDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return UNITS.get(id);
    }

    public static Dossier findDossier(long id) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(VirtualDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DOSSIERS.get(id);
    }

}
