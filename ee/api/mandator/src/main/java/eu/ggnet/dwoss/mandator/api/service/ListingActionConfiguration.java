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

import eu.ggnet.dwoss.common.api.values.SalesChannel;

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
        LOCAL, FTP, MAIL;
    }

    public final Type type;

    public final Location location;

    public final SalesChannel channel;

    public final String name;

    public ListingActionConfiguration(Type type, Location location, SalesChannel channel, String name) {
        this.type = type;
        this.location = location;
        this.channel = channel;
        this.name = name;
    }

    @Override
    public String toString() {
        return "ListingActionConfiguration{" + "type=" + type + ", location=" + location + ", channel=" + channel + ", name=" + name + '}';
    }

}
