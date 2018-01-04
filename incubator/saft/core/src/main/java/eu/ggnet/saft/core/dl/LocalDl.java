/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.core.dl;

import java.util.Optional;

/**
 *
 * @author oliver.guenther
 */
public class LocalDl {

    private static LocalDl instance;

    public static LocalDl getInstance() {
        if ( instance == null ) instance = new LocalDl();
        return instance;
    }

    public <T> T lookup(Class<T> clazz) {
        return null;
    }

    public <T> Optional<T> optional(Class<T> clazz) {
        return Optional.empty();
    }

    public <T> void add(Class<T> clazz, T t) {

    }

}
