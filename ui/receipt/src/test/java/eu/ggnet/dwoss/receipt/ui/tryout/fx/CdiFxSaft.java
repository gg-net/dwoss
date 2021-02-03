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
package eu.ggnet.dwoss.receipt.ui.tryout.fx;

import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.impl.Fx;
import eu.ggnet.saft.core.ui.LocationStorage;

/**
 * CDI Saft with FX.
 *
 * @author mirko.schulze
 */
@ApplicationScoped
// @Specializes
public class CdiFxSaft extends Saft {

    @Inject
    private Instance<Object> instance;

    public CdiFxSaft() {
        super(new LocationStorage(), Executors.newCachedThreadPool());
    }

    @PostConstruct
    private void postInit() {
        init(new Fx(this, p -> instance.select(p).get()));
        core().captureMode(true);
    }

}
