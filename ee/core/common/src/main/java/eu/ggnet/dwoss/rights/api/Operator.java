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
package eu.ggnet.dwoss.rights.api;

import java.io.Serializable;
import java.util.*;

import lombok.Value;

/**
 * This is a Data Transfer Object for {@link Operator}.
 * <p>
 * @author Bastian Venz
 * <p>
 */
@Value
public class Operator implements Serializable {

    private final String username;

    private final int quickLoginKey;

    private final List<AtomicRight> rights;

}
