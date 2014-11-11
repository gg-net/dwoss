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
package eu.ggnet.dwoss.util;

/**
 * Contains Util methods, which on error result in an exception and an slf4j log entry.
 *
 * @author oliver.guenther
 * @deprecated use Objects.notNull() See http://overload.ahrensburg.gg-net.de/jira/browse/DW-1152
 */
@Deprecated
public class AssertUtilsExcpetionAndLogging {

    @Deprecated
    public static void notNull(Object o) throws NullPointerException {
        notNull(o, null);
    }

    @Deprecated
    public static void notNull(Object o, String name) throws NullPointerException {
        if ( o == null ) {
            String msg = "Name=" + name;
            throw new NullPointerException(msg);
        }
    }
}
