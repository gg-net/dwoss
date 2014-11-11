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
package eu.ggnet.dwoss.util;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class TempUtil {

    private static File tryPath(File path, String subPath) {
        if ( path.isDirectory() && path.canWrite() ) {
            File outputPath = new File(path, subPath);
            if ( !outputPath.exists() ) {
                if ( !outputPath.mkdirs() ) {
                    return null;
                }
            } else if ( !(outputPath.isDirectory() && outputPath.canWrite()) ) {
                return null;
            }
            return outputPath;
        }
        return null;
    }

    /**
     * Tryies to find a location for Temporary files. Optionaly creates the supplied name as Directory an returns a handle.
     *
     * @param name the desired name of the directory
     * @return a handle to a temp directory
     * @throws RuntimeException if somthing goes wrong.
     */
    public static File getDirectory(String name) throws RuntimeException {
        File outputPath = null;
        if ( SystemUtils.JAVA_IO_TMPDIR != null )
            outputPath = tryPath(new File(SystemUtils.JAVA_IO_TMPDIR), name);
        if ( outputPath == null )
            outputPath = tryPath(new File(SystemUtils.USER_HOME), "Temp/" + name);
        if ( outputPath == null ) {
            if ( SystemUtils.IS_OS_WINDOWS ) {
                outputPath = tryPath(new File("C:/Temp/"), name);
                if ( outputPath == null ) outputPath = tryPath(new File("D:/Temp/"), name);
            }
        }
        if ( outputPath == null ) throw new RuntimeException("No usable Templocation found, giving up");
        return outputPath;
    }
}
