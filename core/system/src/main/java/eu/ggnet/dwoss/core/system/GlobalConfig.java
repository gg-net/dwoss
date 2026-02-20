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
package eu.ggnet.dwoss.core.system;

import jakarta.enterprise.inject.Produces;

import eu.ggnet.dwoss.core.common.values.TaxType;
import eu.ggnet.dwoss.core.system.util.Utils;

import jakarta.enterprise.context.ApplicationScoped;

import static eu.ggnet.dwoss.core.common.values.TaxType.GENERAL_SALES_TAX_DE_19_PERCENT;

/**
 * Constants, which normaly whould be confiured in the application.
 * Before adding something new here, really really think, if that makes sense.
 * <p>
 * @author oliver.guenther
 */
public final class GlobalConfig {

    @ApplicationScoped
    public static class GlobalConfigProducer {

        @Produces
        @OutputPath
        public String produceApplicationOutputPath() {
            return APPLICATION_PATH_OUTPUT;
        }
    }

    /**
     * A central place there the client can put exports.
     */
    public static final String APPLICATION_PATH_OUTPUT = Utils.getTempDirectory("output") + "/";

    /**
     * The actual tax.
     * Not really good here, but acceptable for now.
     */
    public static final TaxType DEFAULT_TAX = GENERAL_SALES_TAX_DE_19_PERCENT;

    /**
     * Default country, is used for printing information.
     * Used in the Addresslabels, so if the country is equal to this one it is not printed.
     */
    public static final String LOCAL_ISO_COUNTRY = "DE";

    /**
     * Apiversion. Esures, that the client and the Server are compatible.
     */
    public static final int API_VERSION = 2; 
   
}
