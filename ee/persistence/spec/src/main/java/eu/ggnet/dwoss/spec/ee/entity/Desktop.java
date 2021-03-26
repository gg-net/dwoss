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

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import eu.ggnet.dwoss.core.common.INoteModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import static eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra.*;
import static javax.persistence.CascadeType.*;

/**
 * Represents a {@link Desktop} computer.
 * <p>
 * @has 1 - n Cpu
 * @has 1 - n Gpu
 * @has n - m Desktop.Hdd
 * @has n - m Desktop.Odd
 * @has 1 - n Desktop.Os
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Desktop extends BasicSpec {

    /**
     * Allowed values for Memory Size.
     */
    public final static Integer[] MEMORY_SIZES = {0, 512, 1024, 2048, 3072, 4096, 5120, 6144,
        7268, 8192, 10240, 12288, 16384, 24576, 32768, 65536};

    public static enum OsCategory {

        MISC(
                Os.LINUX,
                Os.FREE_DOS,
                Os.ANDROID,
                Os.CHROME_OS,
                Os.I_OS,
                Os.MAC_OS_X,
                Os.WATCH_OS,
                Os.WINDOWS_HOME_SERVER,
                Os.WINDOWS_XP_PROFESSIONAL,
                Os.WINDOWS_XP_PROFESSIONAL_64,
                Os.WINDOWS_XP_HOME,
                Os.WINDOWS_XP_TABLET_PC,
                Os.WINDOWS_XP_MEDIA_CENTER_EDITION,
                Os.WINDOWS_VISTA_HOME_BASIC_32,
                Os.WINDOWS_VISTA_HOME_PREMIUM_32,
                Os.WINDOWS_VISTA_HOME_PREMIUM_64,
                Os.WINDOWS_VISTA_BUSINESS_32,
                Os.WINDOWS_VISTA_ULTIMATE_32),
        WINDOWS_7(
                Os.WINDOWS_7_STARTER_32,
                Os.WINDOWS_7_HOME_BASIC_32,
                Os.WINDOWS_7_HOME_PREMIUM_32,
                Os.WINDOWS_7_HOME_PREMIUM_64,
                Os.WINDOWS_7_PROFESSIONAL_32,
                Os.WINDOWS_7_PROFESSIONAL_64,
                Os.WINDOWS_7_ULTIMATE_32,
                Os.WINDOWS_7_ULTIMATE_64,
                Os.WINDOWS_7_EMBEDDED),
        WINDOWS_8(
                Os.WINDOWS_8_RT,
                Os.WINDOWS_8_32,
                Os.WINDOWS_8_64,
                Os.WINDOWS_8_PROFESSIONAL_32,
                Os.WINDOWS_8_PROFESSIONAL_64,
                Os.WINDOWS_8_1_32,
                Os.WINDOWS_8_1_64,
                Os.WINDOWS_8_1_PROFESSIONAL_32,
                Os.WINDOWS_8_1_PROFESSIONAL_64),
        WINDOWS_10(
                Os.WINDOWS_10_HOME_32,
                Os.WINDOWS_10_HOME_64,
                Os.WINDOWS_10_PRO_32,
                Os.WINDOWS_10_PRO_64,
                Os.WINDOWS_10_S_32,
                Os.WINDOWS_10_S_64
        );

        private Os[] oss;

        private OsCategory(Os... oss) {
            this.oss = oss;
        }

        public Os[] getOss() {
            return oss;
        }
    }

    /**
     *
     * @has 1 - n Desktop.OsCategory
     */
    public static enum Os implements INoteModel {

        LINUX("Linux"),
        WINDOWS_XP_HOME("Windows XP Home"),
        WINDOWS_XP_PROFESSIONAL("Windows XP Professional"),
        WINDOWS_XP_TABLET_PC("Windows XP Tablet PC Edition"),
        WINDOWS_XP_PROFESSIONAL_64("Windows XP Professional 64"),
        WINDOWS_XP_MEDIA_CENTER_EDITION("Windows XP Media Center Edition"),
        WINDOWS_VISTA_HOME_BASIC_32("Windows Vista Home Basic 32"),
        WINDOWS_VISTA_HOME_PREMIUM_32("Windows Vista Home Premium 32"),
        WINDOWS_VISTA_HOME_PREMIUM_64("Windows Vista Home Premium 64"),
        WINDOWS_VISTA_BUSINESS_32("Windows Vista Business 32"),
        WINDOWS_VISTA_ULTIMATE_32("Windows Vista Ultimate 32"),
        WINDOWS_7_HOME_PREMIUM_32("Windows 7 Home Premium 32"),
        WINDOWS_7_HOME_PREMIUM_64("Windows 7 Home Premium 64"),
        WINDOWS_7_HOME_BASIC_32("Windows 7 Home basic 32"),
        WINDOWS_7_STARTER_32("Windows 7 Starter"),
        WINDOWS_7_PROFESSIONAL_32("Windows 7 Professional 32"),
        WINDOWS_7_PROFESSIONAL_64("Windows 7 Professional 64"),
        WINDOWS_7_ULTIMATE_32("Windows 7 Ultimate 32"),
        WINDOWS_7_ULTIMATE_64("Windows 7 Ultimate 64"),
        FREE_DOS("FreeDos"),
        ANDROID("Android"),
        WINDOWS_HOME_SERVER("Windows Home Server"),
        WINDOWS_8_RT("Windows 8 RT"),
        WINDOWS_8_32("Windows 8 32"),
        WINDOWS_8_64("Windows 8 64"),
        WINDOWS_8_PROFESSIONAL_32("Windows 8 Professional 32"),
        WINDOWS_8_PROFESSIONAL_64("Windows 8 Professional 64"),
        CHROME_OS("Chrome OS"),
        I_OS("iOS"),
        MAC_OS_X("Mac OS X"),
        WINDOWS_8_1_64("Windows 8.1 64"),
        WINDOWS_8_1_32("Windows 8.1 32"),
        WINDOWS_7_EMBEDDED("Windows 7 Embedded Standard"),
        WINDOWS_10_HOME_32("Windows 10 Home 32"),
        WINDOWS_10_HOME_64("Windows 10 Home 64"),
        WINDOWS_10_PRO_32("Windows 10 Professional 32"),
        WINDOWS_10_PRO_64("Windows 10 Professional 64"),
        WINDOWS_8_1_PROFESSIONAL_32("Windows 8.1 Professional 32"),
        WINDOWS_8_1_PROFESSIONAL_64("Windows 8.1 Professional 64"),
        WINDOWS_10_S_32("Windows 10 S 32"),
        WINDOWS_10_S_64("Windows 10 S 64"),
        WATCH_OS("Watch OS");

        private final String note;

        private final double economicValue;

        private Os(String note) {
            this(note, 0);
        }

        private Os(String note, double economicValue) {
            this.note = note;
            this.economicValue = economicValue;
        }

        @Override
        public String getNote() {
            return note;
        }

        public double getEconomicValue() {
            return economicValue;
        }

        public OsCategory getCategory() {
            for (OsCategory osCategory : OsCategory.values()) {
                for (Os os1 : osCategory.getOss()) {
                    if ( os1.equals(this) ) return osCategory;
                }
            }
            throw new RuntimeException(this + " has no Category");
        }
    }

    public static enum Odd implements INoteModel {

        DVD_SUPER_MULTI("DVD Super Multi"),
        DVD_ROM("DVD-ROM"),
        BLURAY_COMBO("Blu-Ray Combo"),
        BLURAY_SUPER_MULTI("Blu-Ray Super Multi");

        private String note;

        private final double economicValue;

        private Odd(String note) {
            this(note, 0);
        }

        private Odd(String note, double economicValue) {
            this.note = note;
            this.economicValue = economicValue;
        }

        public double getEconomicValue() {
            return economicValue;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    /**
     * @has 1 - n Desktop.Hdd.Type
     */
    public static enum Hdd implements INoteModel {

        SSD_0008(Type.SSD, 8, "8GB SSD"),
        SSD_0016(Type.SSD, 16, "16GB SSD"),
        SSD_0020(Type.SSD, 20, "20GB SSD"),
        SSD_0032(Type.SSD, 32, "32GB SSD"),
        SSD_0060(Type.SSD, 60, "60GB SSD"),
        SSD_0064(Type.SSD, 64, "64GB SSD"),
        SSD_0080(Type.SSD, 80, "80GB SSD"),
        SSD_0090(Type.SSD, 90, "90GB SSD"),
        SSD_0120(Type.SSD, 120, "120GB SSD"),
        SSD_0128(Type.SSD, 128, "128GB SSD"),
        SSD_0160(Type.SSD, 160, "160GB SSD"),
        SSD_0256(Type.SSD, 256, "256GB SSD"),
        SSD_0512(Type.SSD, 512, "512GB SSD"),
        ROTATING_0080(Type.ROTATING, 80, "80GB HDD"),
        ROTATING_0120(Type.ROTATING, 120, "120GB HDD"),
        ROTATING_0160(Type.ROTATING, 160, "160GB HDD"),
        ROTATING_0250(Type.ROTATING, 250, "250GB HDD"),
        ROTATING_0320(Type.ROTATING, 320, "320GB HDD"),
        ROTATING_0500(Type.ROTATING, 500, "500GB HDD"),
        ROTATING_0640(Type.ROTATING, 640, "640GB HDD"),
        ROTATING_0750(Type.ROTATING, 750, "750GB HDD"),
        ROTATING_1000(Type.ROTATING, 1000, "1000GB HDD"),
        ROTATING_1500(Type.ROTATING, 1500, "1500GB HDD"),
        ROTATING_2000(Type.ROTATING, 2000, "2000GB HDD"),
        ROTATING_3000(Type.ROTATING, 3000, "3000GB HDD"),
        SSD_0240(Type.SSD, 240, "240GB SSD"),
        SSD_0004(Type.SSD, 4, "4GB SSD"),
        SSD_0096(Type.SSD, 96, "96GB SSD");

        public static enum Type {

            SSD, ROTATING
        }

        public static Set<Hdd> getByType(Type type) {
            if ( type == null ) return EnumSet.noneOf(Hdd.class);
            Set<Hdd> result = EnumSet.noneOf(Hdd.class);
            for (Hdd hdd : Hdd.values()) {
                if ( hdd.getType() == type ) result.add(hdd);
            }
            return result;
        }

        private Type type;

        private int size;

        private String note;

        private final double economicValue;

        private Hdd(Type type, int size, String note) {
            this(type, size, note, 0);
        }

        private Hdd(Type type, int size, String note, double economicValue) {
            this.type = type;
            this.size = size;
            this.note = note;
            this.economicValue = economicValue;
        }

        public Type getType() {
            return type;
        }

        public int getSize() {
            return size;
        }

        public double getEconomicValue() {
            return economicValue;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    @XmlAttribute
    private Os os;

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Cpu cpu;

    @XmlElement(name = "hdd")
    @XmlElementWrapper
    @NotNull
    @ElementCollection
    private List<Hdd> hdds = new ArrayList<>();

    @XmlElement(name = "odd")
    @XmlElementWrapper
    @NotNull
    @ElementCollection
    private List<Odd> odds = new ArrayList<>();

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Gpu gpu;

    @XmlAttribute
    private int memory;

    public Desktop(String partNo, Long productId) {
        super(partNo, productId);
    }

    public Desktop() {
        this(null, null, null, null, null, 0, null);
    }

    public Desktop(Os os, Cpu cpu, List<Hdd> hdds, Gpu gpu, List<Odd> odds, int memory, Set<Extra> extras) {
        this.hdds = new ArrayList<>();
        this.odds = new ArrayList<>();
        this.os = os;
        this.cpu = cpu;
        this.gpu = gpu;
        this.memory = memory;
        if ( hdds != null ) {
            this.hdds.addAll(hdds);
        }
        if ( odds != null ) {
            this.odds.addAll(odds);
        }
        if ( extras != null ) {
            setExtras(extras);
        }
    }

    @Override
    public Set<Extra> getDefaultExtras() {
        return EnumSet.of(CARD_READER, E_SATA, PS_2, SPEAKERS, USB_3, WLAN_TO_N, WLAN_TO_G, MEDIA_STATION, TV_TUNER, INFRARED_RESCEIVER, DUAL_LOAD,
                BLUETOOTH, THUNDERBOLT, LIGHTNING, WLAN_AC, USB_TYPE_C);
    }

    public Cpu getCpu() {
        return cpu;
    }

    public Desktop setCpu(Cpu cpu) {
        this.cpu = cpu;
        return this;
    }

    public Gpu getGpu() {
        return gpu;
    }

    public void setGpu(Gpu graphics) {
        this.gpu = graphics;
    }

    public List<Hdd> getHdds() {
        return Collections.unmodifiableList(hdds);
    }

    public List<Odd> getOdds() {
        return Collections.unmodifiableList(odds);
    }

    public void setHdds(List<Hdd> hdds) {
        this.hdds = hdds;
    }

    public void setOdds(List<Odd> odds) {
        this.odds = odds;
    }

    public void add(Hdd hdd) {
        hdds.add(hdd);
    }

    public void add(Odd odd) {
        odds.add(odd);
    }

    public void remove(Hdd hdd) {
        hdds.remove(hdd);
    }

    public void remove(Odd odd) {
        odds.remove(odd);
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public Os getOs() {
        return os;
    }

    public void setOs(Os os) {
        this.os = os;
    }

    @Override
    public String toString() {
        return "Desktop{" + super.toString() + ",os=" + os + ", cpu=" + cpu + ", hdds=" + hdds
                + ", odds=" + odds + ", gpu=" + gpu + ", memory=" + memory + '}';
    }

    // Eager init off collections.
    @PostLoad
    private void loadCollections() {
        hdds.size();
        odds.size();
    }
}
