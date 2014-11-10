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
        return EnumSet.of(CARD_READER, E_SATA, PS_2, SPEAKERS, USB_3, WLAN_TO_N, WLAN_TO_G, BLUETOOTH, KAMERA, THREE_D, TOUCH, TV_TUNER, THUNDERBOLT);
    }

    @Override
    public String toString() {
        return "AllInOne{" + super.toString() + '}';
    }
}
