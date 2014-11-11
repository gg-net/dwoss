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
package eu.ggnet.saft.core;

import javax.naming.Context;

import org.netbeans.api.annotations.common.NonNull;

/**
 * Service Interface to the Backend implementation.
 */
public interface Server {

    /**
     * Returns a Enterprise Context, optionally creating one if needed.
     * <p/>
     * @return a Enterprise Context, optionally creating one if needed.
     */
    @NonNull
    Context getContext();

    /**
     * Optionally initialise data after server startup in background.
     * e.g. Generate sample data.
     */
    void initialise();

    /**
     * Shutdown the Backend.
     */
    void shutdown();
}
