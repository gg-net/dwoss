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
package eu.ggnet.dwoss.assembly.remote.select;

import lombok.Getter;

/**
 *
 * @author oliver.guenther
 */
@Getter
public enum RemoteMode {

    GG_NET_TEST("GG-Net Testsystem", "http://obsidian.ahrensburg.gg-net.de:8080/tomee/ejb", "ggnet", "testing"),
    GG_NET_PRODUCTIVE("GG-Net Produktivsystem", "http://retrax.ahrensburg.gg-net.de:8080/tomee/ejb", "ggnet", "productive"),
    ELUS_TEST("Elbe Logistik und Service GmbH Testsystem", "http://obsidian.ahrensburg.gg-net.de:9080/tomee/ejb", "elus", "testing"),
    ELUS_PRODUCTIVE("Elbe Logistik und Service GmbH Produktivsystem", "http://retrax.ahrensburg.gg-net.de:9080/tomee/ejb", "elus", "productive"),
    LOCALHOST("Locale TomEE Connection", "http://localhost:8080/tomee/ejb", "", ""),
    FREE("Direkte Eingabe des Servers", "", "", "");

    private final String description;

    private final String url;

    private final String mandatorKey;

    private final String modeKey;

    private RemoteMode(String description, String url, String mandatorKey, String modeKey) {
        this.description = description;
        this.url = url;
        this.mandatorKey = mandatorKey;
        this.modeKey = modeKey;
    }

    public static RemoteMode find(String mandatorKey, String modeKey) {
        for (RemoteMode mode : RemoteMode.values()) {
            if ( mode.getMandatorKey().equals(mandatorKey) && mode.getModeKey().equals(modeKey) ) {
                return mode;
            }
        }
        return null;
    }

}
