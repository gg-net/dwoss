package eu.ggnet.dwoss.spec.entity;

import java.util.*;

import javax.persistence.Entity;

import eu.ggnet.dwoss.spec.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.entity.piece.*;

import static eu.ggnet.dwoss.spec.entity.ProductSpec.Extra.*;

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
        return EnumSet.of(BLUETOOTH, CARD_READER, FINGER_SCANNER, KAMERA, PENSLOT, UMTS, USB_3, WLAN_TO_G, WLAN_TO_N, BATTERY_INTEGRATED, LTE);
    }

    @Override
    public String toString() {
        return "Tablet{" + super.toString() + '}';
    }
}
