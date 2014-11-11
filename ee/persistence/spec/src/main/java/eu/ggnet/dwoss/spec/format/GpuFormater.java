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
package eu.ggnet.dwoss.spec.format;

import java.util.Iterator;

import eu.ggnet.dwoss.spec.entity.piece.Gpu;

/**
 *
 * @author pascal.perau
 */
public abstract class GpuFormater {

    /**
     * Generate the source for a new Cpu object.
     * The semicolon at the end will not be set.
     * 
     * @param gpu the gpu
     * @return a formated String
     */
    public static String toSource(Gpu gpu){
        if(gpu == null) return "Gpu ist null";
        String types = "";
        for (Iterator<Gpu.Type> it = gpu.getTypes().iterator(); it.hasNext();) {
            types += "Gpu.Type." + it.next();
            if (it.hasNext()) types += ", ";
        }
        return "new Gpu(Gpu.Series." + gpu.getSeries()
                + ", EnumSet.of(" + types +")"
                + ", \"" + gpu.getModel() + "\")";
    }

}
