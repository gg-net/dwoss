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
package eu.ggnet.dwoss.spec.ee.format;

import java.util.Iterator;

import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

public class CpuFormater {

    /**
     * Generate the source for a new Cpu object.
     * The semicolon at the end will not be set.
     * <p>
     * @param cpu the cpu to format
     * @return a string representation of the cpu
     */
    public static String toSource(Cpu cpu) {
        if ( cpu == null ) return "Cpu is null";
        String types = "";
        for (Iterator<Cpu.Type> it = cpu.getTypes().iterator(); it.hasNext();) {
            types += "Cpu.Type." + it.next();
            if ( it.hasNext() ) types += ", ";
        }
        return "new Cpu(Cpu.Series." + cpu.getSeries()
                + ", EnumSet.of(" + types + ")"
                + ", \"" + cpu.getModel() + "\")";
    }
}
