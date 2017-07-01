/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.unit;

import java.net.MalformedURLException;
import java.net.URL;

import eu.ggnet.dwoss.mandator.api.value.partial.Company;
import eu.ggnet.dwoss.mandator.api.value.partial.UrlLocation;

/**
 *
 * @author olive
 */
public class CompanyGen {

    public static Company makeCompany() {
        try {
            return Company.builder()
                    .name("Example GmbH")
                    .street("Test Street 7")
                    .zip("99999")
                    .city("Testcity")
                    .email("test@example.de")
                    .emailName("Example GmbH Shop")
                    .logo(new UrlLocation(new URL("file:///")))
                    .build();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
