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

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.spec.ee.entity.piece.*;

import static javax.persistence.CascadeType.*;

/**
 * Represents an Desktoplike Instance with a Display
 *
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public abstract class DisplayAbleDesktop extends Desktop implements DisplayAble {

    public DisplayAbleDesktop() {
        display = new Display();
    }

    public DisplayAbleDesktop(Os os, Cpu cpu, List<Hdd> hdds, Gpu gpu, List<Odd> odds, int memory, Set<Extra> extras, Display display) {
        super(os, cpu, hdds, gpu, odds, memory, extras);
        this.display = display;
    }

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Display display;

    @Override
    public Display getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(Display display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return "{" + super.toString() + " + display=" + display + '}';
    }

}
