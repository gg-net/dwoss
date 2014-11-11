/* 
 * Copyright (C) 2014 pascal.perau
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

import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.*;

/**
 * Class used as a valueholder for listing actions.
 * <p>
 * @author pascal.perau
 */
@Getter
@ToString
@RequiredArgsConstructor
public class ListingActionConfiguration implements Serializable {

    public enum Type {

        PDF, XLS;

    }

    public enum Location {

        LOCAL, FTP, MAIL;

    }

    private final Type type;

    private final Location location;

    private final SalesChannel channel;

    private final String name;

    /*
     right !!!
     */
}
