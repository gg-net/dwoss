/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.core.widget.cdi;

import javax.enterprise.inject.Produces;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.LocalDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;

/**
 *
 * @author oliver.guenther
 */
public class WidgetProducers {

    @Produces
    public static RemoteDl remoteDl = Dl.remote();

    @Produces
    public static LocalDl localDl = Dl.local();

    @Produces
    public static Guardian guardian() {
        Guardian g = localDl.lookup(Guardian.class);
        LoggerFactory.getLogger(WidgetProducers.class).debug("guardian() produces " + g);
        return g;
    }
}
