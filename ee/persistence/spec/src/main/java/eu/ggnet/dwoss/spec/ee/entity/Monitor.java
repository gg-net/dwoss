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
package eu.ggnet.dwoss.spec.ee.entity;

import java.util.EnumSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import eu.ggnet.dwoss.spec.ee.entity.piece.Display;

import static eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra.*;
import static jakarta.persistence.CascadeType.*;

/**
 * Represents a Monitor.
 *
 * @author pascal.perau
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Monitor extends BasicSpec implements DisplayAble {

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Display display;

    Monitor(long id) {
        super(id);
    }

    public Monitor(Display display) {
        this.display = display;
    }

    public Monitor() {
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(KAMERA, THREE_D, PIVOT, HIGHT_CHANGEABLE, SPEAKERS, TOUCH, IPS_DISPLAY, DISPLAY_120, DISPLAY_144);
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(Display resolutionData) {
        this.display = resolutionData;
    }

    @Override
    public String toString() {
        return "Monitor{" + super.toString() + ", display=" + display + '}';
    }
}
