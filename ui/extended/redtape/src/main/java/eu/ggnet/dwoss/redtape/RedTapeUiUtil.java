package eu.ggnet.dwoss.redtape;

import eu.ggnet.saft.core.Client;

import eu.ggnet.dwoss.redtape.api.LegacyBridge;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.redtape.RedTapeWorker;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeUiUtil {

    /**
     * Returns a HTML view of the dossier either from a legacy system or redtape.
     * <p>
     * @param dos the dossier
     * @return a HTML view.
     */
    public static String toHtmlDetailed(Dossier dos) {
        if ( dos.isLegacy() && Client.hasFound(LegacyBridge.class) ) {
            return lookup(LegacyBridge.class).toDetailedHtmlDossier(dos.getLegacyIdentifier());
        } else {
            return lookup(RedTapeWorker.class).toDetailedHtml(dos.getId());
        }
    }
}
