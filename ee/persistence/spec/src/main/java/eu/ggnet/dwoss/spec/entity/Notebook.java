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
package eu.ggnet.dwoss.spec.entity;

import java.util.*;

import javax.persistence.Entity;

import eu.ggnet.dwoss.spec.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.entity.piece.*;

import static eu.ggnet.dwoss.spec.entity.ProductSpec.Extra.*;

/**
 * Represents a Notebook. Systematically compounded of a {@link Desktop} and {@link Display}.
 * <p>
 * @author pascal.perau
 */
@Entity
public class Notebook extends DisplayAbleDesktop {

    public Notebook() {
    }

    public Notebook(Display display, Os os, Cpu cpu, List<Hdd> hdds, Gpu graphics, List<Odd> odds, int memory, Set<Extra> extraparts) {
        super(os, cpu, hdds, graphics, odds, memory, extraparts, display);
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(BLUETOOTH, CARD_READER, FINGER_SCANNER, CONVERTABLE, KAMERA, PENSLOT, THREE_D, TOUCH,
                UMTS, USB_3, WLAN_TO_G, WLAN_TO_N, BATTERY_INTEGRATED, DUAL_DISPLAY_TABLET, LTE, DUAL_LOAD, THUNDERBOLT);
    }

    @Override
    public String toString() {
        return "Notebook{" + super.toString() + '}';
    }
}
