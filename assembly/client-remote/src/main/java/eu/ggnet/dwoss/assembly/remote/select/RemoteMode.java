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
