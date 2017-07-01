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
package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import lombok.*;

/**
 * A serializable (for wildfly) wrapper for URL.
 *
 * @author oliver.guenther
 */
@ToString
@EqualsAndHashCode
public class UrlLocation implements Serializable {

    @Getter
    private String location;

    public UrlLocation(@NotNull URL url) {
        location = Objects.requireNonNull(url, "Url must not be null").toString();
    }

    /**
     * Returs a URL of the internal location.
     *
     * @return a URL of the internal location
     */
    public URL toURL() {
        try {
            return new URL(location);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("UrlLocation=" + location + " cannot be converted to an URL. This should never happen");
        }
    }

}
