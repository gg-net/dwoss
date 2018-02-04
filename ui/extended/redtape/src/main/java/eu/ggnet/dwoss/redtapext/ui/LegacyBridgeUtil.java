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
package eu.ggnet.dwoss.redtapext.ui;

import eu.ggnet.dwoss.redtape.ee.api.LegacyRemoteBridge;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtapext.ee.RedTapeWorker;
import eu.ggnet.saft.Dl;

/**
 *
 * @author oliver.guenther
 */
public class LegacyBridgeUtil {

    /**
     * Returns a HTML view of the dossier either from a legacy system or redtape.
     * <p>
     * @param dos the dossier
     * @return a HTML view.
     */
    public static String toHtmlDetailed(Dossier dos) {
        if ( dos.isLegacy() && Dl.remote().contains(LegacyRemoteBridge.class) ) {
            return Dl.remote().lookup(LegacyRemoteBridge.class).toDetailedHtmlDossier(dos.getLegacyIdentifier());
        } else {
            return Dl.remote().lookup(RedTapeWorker.class).toDetailedHtml(dos.getId());
        }
    }
}
