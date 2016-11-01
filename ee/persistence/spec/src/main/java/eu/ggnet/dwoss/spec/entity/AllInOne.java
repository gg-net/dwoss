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
package eu.ggnet.dwoss.spec.entity;

import java.util.*;

import javax.persistence.Entity;

import eu.ggnet.dwoss.spec.entity.piece.*;

import static eu.ggnet.dwoss.spec.entity.ProductSpec.Extra.*;

/**
 * Represents a AllInOne Desktop.
 *
 * @author pascal.perau
 */
@Entity
public class AllInOne extends DisplayAbleDesktop {

    public AllInOne() {
    }

    public AllInOne(Display display, Os os, Cpu cpu, List<Hdd> hdds, Gpu graphics, List<Odd> odds, int memory, Set<Extra> extraparts) {
        super(os, cpu, hdds, graphics, odds, memory, extraparts, display);
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(
                CARD_READER, E_SATA, PS_2, SPEAKERS, USB_3, WLAN_TO_N, WLAN_TO_G, WLAN_AC, BLUETOOTH,
                KAMERA, THREE_D, TOUCH, TV_TUNER, THUNDERBOLT, IPS_DISPLAY, BATTERY_INTEGRATED, KEYBOARD_BACKGROUND_LIGHT);
    }

    @Override
    public String toString() {
        return "AllInOne{" + super.toString() + '}';
    }
}
