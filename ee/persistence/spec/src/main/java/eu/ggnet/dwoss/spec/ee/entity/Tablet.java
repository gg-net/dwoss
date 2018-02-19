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

import java.util.*;

import javax.persistence.Entity;

import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;

import static eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra.*;

/**
 * A Tablet.
 *
 * @author oliver.guenther
 */
@Entity
public class Tablet extends DisplayAbleDesktop {

    public Tablet() {
    }

    public Tablet(Display display, Desktop.Os os, Cpu cpu, List<Desktop.Hdd> hdds, Gpu graphics, List<Desktop.Odd> odds, int memory, Set<Extra> extraparts) {
        super(os, cpu, hdds, graphics, odds, memory, extraparts, display);
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(BLUETOOTH, CARD_READER, FINGER_SCANNER, KAMERA, UMTS, USB_3, WLAN_TO_G, WLAN_TO_N, WLAN_AC, BATTERY_INTEGRATED,
                LTE, COVER, GORILLA_GLASS, DUAL_SIM, LIGHTNING, IPS_DISPLAY, USB_TYPE_C, BLUE_LIGHT_FILTER, REALSENSE_3D_CAM, ITEGATED_SIM);
    }

    @Override
    public String toString() {
        return "Tablet{" + super.toString() + '}';
    }
}
