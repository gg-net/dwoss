/*
 * Copyright (C) 2015 GG-Net GmbH
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

import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Misc Utils, split if too much.
 * <p>
 * @author oliver.guenther
 */
public class Utils {

    /**
     * Returns the 1st external ipv4 address.
     * <p>
     * @return the 1st external ipv4 address.
     */
    public static InetAddress externalIpv4Address() {
        try {
            Enumeration<NetworkInterface> netInter = NetworkInterface.getNetworkInterfaces();
            int n = 0;

            while (netInter.hasMoreElements()) {
                NetworkInterface ni = netInter.nextElement();
                for (InetAddress iaddress : Collections.list(ni.getInetAddresses())) {
                    if ( iaddress.isLoopbackAddress() ) continue;
                    if ( !(iaddress instanceof Inet4Address) ) continue;
                    return iaddress;
                }
            }
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalStateException("No External Ip found, sure u are in the net ?");
    }

    public static void main(String[] args) {
        System.out.println(externalIpv4Address().getHostAddress());
    }

}
