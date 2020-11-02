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
package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;
import java.util.Objects;

import eu.ggnet.dwoss.core.common.values.SalesChannel;

/**
 * Class used as a valueholder for listing actions.
 * <p>
 * @author pascal.perau
 */
public class ListingActionConfiguration implements Serializable {

    public enum Type {
        PDF, XLS;
    }

    public enum Location {
        LOCAL, REMOTE;
    }

    public final Type type;

    public final Location location;

    public final SalesChannel channel;

    public final String name;

    public ListingActionConfiguration(Type type, Location location, SalesChannel channel, String name) {
        this.type = Objects.requireNonNull(type, "new ListingActionConfiguration with type=null called, not allowed");;
        this.location = Objects.requireNonNull(location, "new ListingActionConfiguration with location=null called, not allowed");;
        this.channel = Objects.requireNonNull(channel, "new ListingActionConfiguration with channel=null called, not allowed");;
        this.name = Objects.requireNonNull(name, "new ListingActionConfiguration with name=null called, not allowed");;
    }

    @Override
    public String toString() {
        return "ListingActionConfiguration{" + "type=" + type + ", location=" + location + ", channel=" + channel + ", name=" + name + '}';
    }

}
