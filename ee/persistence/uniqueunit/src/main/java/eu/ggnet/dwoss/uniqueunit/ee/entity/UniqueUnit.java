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
package eu.ggnet.dwoss.uniqueunit.ee.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import eu.ggnet.dwoss.core.common.INoteModel;
import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;
import eu.ggnet.dwoss.core.system.util.TwoDigits;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Equipment.*;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static jakarta.persistence.CascadeType.*;

/**
 * A UniqueUnit represents exactly one unit that is unique in the whole system.
 * Identefiable through {@link Identifier}
 *
 * @has 1 - n UniqueUnitHistory
 * @has n - m UniqueUnit.Equipment
 * @has n - m UniqueUnit.StaticComment
 * @has n - m UniqueUnit.StaticInternalComment
 * @has n - 1 UniqueUnit.Condition
 * @has n - 1 SalesChannel
 * @has 0 - n PriceHistory
 * @has 1 - 1 Identifier
 * @has n - m Flag
 */
@Entity
@NamedQuery(name = "UnqiueUnit.findByIdenfiersTypeValue", query = "SELECT u FROM UniqueUnit u join u.identifiers i WHERE KEY(i) = ?1 and VALUE(i) IN (?2)")
@NamedQuery(name = "UnqiueUnit.betweenInputDates", query = "SELECT u FROM UniqueUnit u WHERE u.inputDate >= ?1 AND u.inputDate <= ?2")
@NamedQuery(name = "UnqiueUnit.betweenInputDatesAndContractor", query = "SELECT u FROM UniqueUnit u WHERE u.inputDate >= ?1 AND u.inputDate <= ?2 and u.contractor = ?3")
@NamedQuery(name = "UniqueUnit.findByIds", query = "SELECT u FROM UniqueUnit u WHERE u.id IN (:idList)")
@NamedQuery(name = "UniqueUnit.byProductPartNo", query = "SELECT u FROM UniqueUnit u WHERE u.product.partNo = ?1")
@NamedQuery(name = "UniqueUnit.byProductPartNosInputDate", query = "SELECT u FROM UniqueUnit u WHERE u.product.partNo in (?1) AND u.inputDate >= ?2 AND u.inputDate <= ?3")
@NamedQuery(name = "UnqiueUnit.byContractor", query = "SELECT u FROM UniqueUnit u WHERE u.contractor = ?1")
@NamedQuery(name = "UniqueUnit.countByInputDateContractor",
            query = "select new eu.ggnet.dwoss.uniqueunit.ee.eao.CountHolder(u.inputDate, u.product.tradeName, u.contractor, count(u.id)) "
            + "from UniqueUnit u where u.inputDate >= :start and u.inputDate <= :end GROUP BY u.contractor, u.product.tradeName, cast(u.inputDate as date)")
@SuppressWarnings({"PersistenceUnitPresent", "JPQLValidation"})     // HINT: Netbeans Warinig is wrong. 2. HINT: Hibernate builds defective Query.
public class UniqueUnit implements Serializable, EagerAble {

    public static NavigableMap<String, UniqueUnit> asMapByRefurbishId(Collection<UniqueUnit> uus) {
        return new TreeMap<>(uus.stream().collect(Collectors.toMap(uu -> uu.getIdentifier(REFURBISHED_ID), uu -> uu)));
    }

    private final static DateFormat MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM);

    /**
     * The equipment parts a UniqueUnit may have.
     */
    public static enum Equipment implements INoteModel {

        POWER_CABLE("Stromkabel"),
        PLUGIN_AC_ADAPTER("Steckernetzteil"), // NB
        AC_ADAPTER_INC_CABLE("Netzteil inkl. Stromkabel"), //NB
        BATTERY("Akku"),
        REMOTE("Fernbedienung"),
        EXT_ANTENNA("externe Antenne"),
        KEYBOARD("Tastatur"),
        MOUSE("Maus"),
        GAME_KEYBOARD("Gaming-Tastatur"),
        GAME_MOUSE("Gaming-Maus"),
        CABLELES_KEYBOARD("Funktastatur"),
        CABLELES_MOUSE("Funkmaus"),
        ANTENNA_ADAPTER("Antennenadapter"),
        DONGLE("Dongle"),
        INSTALL_DISCS("Installations CDs"),
        VGA_CABLE("VGA-Kabel"),
        DVI_CABLE("DVI-Kabel"),
        HDMI_CABLE("HDMI-Kabel"),
        FOOT("Fuß"),
        DISPLAY_FOOT_CONNECTOR("Bein"),
        THREE_D_GLASSES("3D Brille"),
        ORIGINAL_BOXED("Original Karton"),
        KEYBOARD_DOCK("Dockingstation mit Tastatur"),
        USB_KABEL("USB-Kabel"),
        VGA_NETWORK_ADAPTER("VGA-/Netzwerkadapter"),
        MICRO_USB_TO_USB_ADAPTER("Micro USB - USB Adapter"),
        WLAN_KINK_ANTENNA("WLAN Knickantenne"),
        PORTFOLIO_CASE("Portfolio Case"),
        SDCARD_16GB("16GB SD Karte"),
        HDMI_VGA_ADAPTER("HDMI-VGA Adapter"),
        USB_NETWORK_ADAPTER("USB Netzwerk Adapter"),
        PORTFOLIO_CASE_INTEGRATED_KEYBOARD("Portfolio Case inkl. Tastatur"),
        SIM_OPENER("SIM-Kartenöffner"),
        WALLHOLDER("Wandhalterung"),
        VGA_USB_NETWORK_ADAPTER("VGA-/USB-/Netzwerk Adapter"),
        USB_MICRO_HDMI_ADAPTER("USB- Micro HDMI Adapter"),
        ALTERNATIBVE_USB_CABLE("alternativ USB-Kabel"),
        HEADSET("Headset"),
        HEADPHONE("Kopfhörer"),
        DVI_HDMI_ADAPTOR("DVI-HDMI Adapter"),
        MANUAL("Bedienungsanleitung"),
        ALTERNATIVE_BOXED("Alternativverpackung"),
        CRUNCHCOVER("Crunchcover"),
        ALTERNATIVE_PLUGIN_AC_ADAPTER("Alternativsteckernetzteil"),
        MS_OFFICE_365_PERSONAL("Microsoft Office 365 Personal inkl."),
        MS_OFFICE_HOME_AND_STUDENT_2013("Microsoft Office Home and Student 2013 inkl."),
        STYLUS("Stylus / Displaystift"),
        DISPLAYPORT_CABLE("Displayportkabel"),
        VGA_USB_ADAPTER("VGA-USB Adapter"),
        MAGNETIC_CHARGING_CABLE("magnetisches Ladekabel"),
        SPARE_STRAP("Sportarmband – anpassbar für die Längen S/M oder M/L"),
        LC_ADAPTER("Lightning-Klinke Adapter"),
        TRIPOD("Tripod"),
        LC_HEADSET("Headset (Lightnig Anschluss)"),
        INTEGRATED_STYLUS("integrierter Displaystift"),
        HDD_CARRIER("Festplattenhalterung"),
        DESIGN_GLASS_COVER("Designglasblende"),
        LOCKING_SCREW("Feststellschraube"),
        AIRPODS_USB_CABLE("AirPods USB-Kabel"),
        SILICON_HEADS("Silikontips"),
        SMART_CASE("Smart Case"),
        EAR_CUSHION("Ohrpolster");

        private final String note;

        private Equipment(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

        public static Set<Equipment> valueSet() {
            return Equipment.valueSet(null, null);
        }

        /**
         * Returns a Set of Equipment filtered by the {@link ProductGroup}.
         * <p>
         * @param group the {@link ProductGroup}
         * @param name  an optional name of the Product. May be null.
         * @return a Set of Equipment filtered by the {@link ProductGroup}.
         */
        public static Set<Equipment> valueSet(ProductGroup group, String name) {
            if ( group == null ) {
                return EnumSet.allOf(Equipment.class);
            }
            switch (group) {
                case TV:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, HDMI_CABLE, FOOT, DISPLAY_FOOT_CONNECTOR,
                            THREE_D_GLASSES, REMOTE, MANUAL, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE);
                case ALL_IN_ONE:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, MOUSE, KEYBOARD, GAME_MOUSE, GAME_KEYBOARD, CABLELES_KEYBOARD, CABLELES_MOUSE, ANTENNA_ADAPTER,
                            DONGLE, FOOT, DISPLAY_FOOT_CONNECTOR, THREE_D_GLASSES, REMOTE, AC_ADAPTER_INC_CABLE, WALLHOLDER, MANUAL, ALTERNATIVE_PLUGIN_AC_ADAPTER,
                            ALTERNATIBVE_USB_CABLE, USB_KABEL);
                case DESKTOP_BUNDLE:
                    Set<Equipment> equipments = Equipment.valueSet(ProductGroup.DESKTOP, null);
                    equipments.addAll(Equipment.valueSet(ProductGroup.MONITOR, null));
                    return equipments;
                case SERVER:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, MOUSE, KEYBOARD, MANUAL, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE);
                case DESKTOP:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, MOUSE, KEYBOARD, GAME_MOUSE, GAME_KEYBOARD,
                            CABLELES_KEYBOARD, CABLELES_MOUSE, DONGLE, AC_ADAPTER_INC_CABLE, FOOT, HDD_CARRIER, DESIGN_GLASS_COVER);
                case MONITOR:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, AC_ADAPTER_INC_CABLE, VGA_CABLE, DVI_CABLE,
                            HDMI_CABLE, FOOT, DISPLAY_FOOT_CONNECTOR, USB_KABEL, DISPLAYPORT_CABLE, PLUGIN_AC_ADAPTER, REMOTE,
                            WALLHOLDER, LOCKING_SCREW);
                case TABLET_SMARTPHONE:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER, USB_KABEL, LC_HEADSET,
                            SIM_OPENER, STYLUS);
                case NOTEBOOK:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER, AC_ADAPTER_INC_CABLE, STYLUS, INTEGRATED_STYLUS);
                case PROJECTOR:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, REMOTE, VGA_CABLE, HDMI_CABLE, DONGLE);
                case MISC:
                    if ( name == null ) {
                        return EnumSet.allOf(Equipment.class);
                    } else if ( name.contains("AirPods") ) {
                        return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, AIRPODS_USB_CABLE, SILICON_HEADS, SMART_CASE, EAR_CUSHION);
                    } else if ( name.contains("HomePod") ) {
                        return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER);
                    } else if ( name.contains("Watch") ) {
                        return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER, MAGNETIC_CHARGING_CABLE, SPARE_STRAP);
                    } else if ( name.contains("Keyboard") ) {
                        return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED);
                    } else if ( name.contains("Apple TV") ) {
                        return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, USB_KABEL, REMOTE);
                    }
                default:
            }
            return EnumSet.allOf(Equipment.class);
        }
    }

    /**
     * Most common standart notes that a unit may have.
     */
    public static enum StaticComment implements INoteModel {

        FRONT_COVER_MISSING("Untere Fronklappe fehlt"),
        USED_KEABOARD("abgenutzte Tastatur"),
        USED_TOUCHPAD("abgenutztes Touchpad"),
        SCRATCHES_DISPLAYCOVER("Kratzer auf dem Displayrahmen"),
        SCRATCHES_DISPLAY("Kratzer auf dem Display"),
        DISPLAY_PIXERROR("Pixelfehler festgestellt"),
        SCRATCHES_COVER("Kratzer auf dem Deckel"),
        SCRATCHES_KEYBOARD("Kratzer um die Tastatur"),
        SCRATCHES_TOUCH("Kratzer um das Touchpad"),
        USETRACES_DISPLAYCOVER("Gebrauchsspuren auf dem Displayrahmen"),
        USETRACES_COVER("Gebrauchsspuren auf dem Deckel"),
        USETRACES_KEYBOARD("Gebrauchsspuren um die Tastatur"),
        USETRACES_AROUND_TOUCH("Gebrauchsspuren um das Touchpad"),
        SCRATCHES_CASE("Kratzer auf dem Gehäuse"),
        DENT_SOUND("Delle/n auf der Lautsprecherabdeckung"),
        DENT_COVER("Delle/n auf dem Deckel"),
        DENT_CASE("Delle/n auf dem Gehäuse"),
        MINIMAL_USETRACES_COVER("minimale Gebrauchsspuren auf dem Deckel"),
        USETRACES_ABOVE_KEYBOARD("Gebrauchsspuren über der Tastatur"),
        SMALL_USETRACES_ABOVE_KEYBOARD("leichte Gebrauchsspuren über der Tastatur"),
        SCRATCHES_ABOVE_KEYBOARD("Kratzer über der Tastatur"),
        SCRATCHES_ON_CASE_SIDE("Kratzer auf dem Gehäuserand"),
        SCRATCHES_ON_CASE_BOTTOM_SIDE("Kratzer auf der Geräteunterseite"),
        USETRACES_AROUND_KEYBOARD("Kratzer am Gehäuserand um die Tastatur"),
        SIGNIFICANT_TRACES_AROUND_TOUCH("deutliche Gebrauchsspuren um das Touchpad"),
        SLIGHT_USETRACES_COVER("leichte Gebrauchsspuren auf dem Deckel"),
        SCRATCHES_ON_CASE_BELOW_KEABOARD("Kratzer am Gehäuserand unter der Tastatur."),
        SLIGHT_USETRACES_AROUND_TOUCH("leichte Gebrauchsspuren um das Touchpad"),
        MINIMAL_USETRACES_AROUND_TOUCH("minimale Gebrauchsspuren um das Touchpad"),
        SIGNIFICANT_SCRATCHES_COVER("deutliche Kratzer auf dem Deckel"),
        SIGNIFICANT_SCRATCHES_DISPLAYCOVER("deutliche Kratzer auf dem Displayrahmen"),
        SIGNIFICANT_SCRATCHES_AROUND_KEYBOARD("deutliche Kratzer um die Tastatur"),
        SIGNIFICANT_SCRATCHES_KEYBOARD("deutliche Kratzer über der Tastatur"),
        SIGNIFICANT_USETRACES_DISPLAYCOVER("deutliche Gebrauchsspuren auf dem Displayrahmen"),
        SIGNIFICANT_USETRACES_COVER("deutliche Gebrauchsspuren auf dem Deckel"),
        SCRATCHES_ON_COVER_EDGE("Kratzer am Deckelrand"),
        SCRATCHES_ON_TOUCH("Kratzer auf dem Touchpad"),
        SLIGHT_SCRATCHES_ON_DISPLAYCOVER("leichte Kratzer auf dem Displayrahmen"),
        SCRATCHES_ON_THE_BACK("Kratzer auf der Geräterückseite"),
        USETRACES_ON_THE_BACK("Gebrauchsspuren auf der Geräterückseite"),
        SLIGHT_USETRACES_ON_DISPLAYCOVER("leichte Gebrauchsspuren auf dem Displayrahmen"),
        SLIGHT_SCRATCHES_ON_COVER("leichte Kratzer auf dem Deckel"),
        SIGNIFICANT_SCRATCHES_ON_CASE("deutliche Kratzer auf dem Gehäuse"),
        SLIGHT_SCRATCHES_ON_CASE("leichte Kratzer auf dem Gehäuse"),
        SCRATCHES_ON_FOOT("Kratzer auf dem Fuß"),
        FOREIGN_PARTICLES_BELOW_DISPLAY("Fremdpartikel unter dem Display"),
        SLIGHT_SCRATCHES_ON_CASE_BOTTOM_SIDE("leichte Kratzer auf der Geräteunterseite"),
        SCRATCHES_ON_DISPLAY_STAND("Kratzer auf dem Displaybein"),
        SCRATCHES_BELOW_KEYBOARD("Kratzer unterhalb der Tastatur"),
        REMAINS_OF_GLUE_BOTTOM_SIDE("Klebereste auf der Geräteunterseite"),
        SLIGHT_SCRATCHES_AROUND_TOUCHPAD("leichte Kratzer um das Touchpad"),
        USETRACES_ON_CASE_BOTTOM_SIDE("Gebrauchsspuren auf der Geräteunterseite"),
        USETRACES_DISPLAY("Gebrauchsspuren auf dem Display"),
        FOOT_OR_STAND_MISSING("Fuß/Bein fehlt"),
        USETRACES_ON_THE_LOADINGCASE("Gebrauchsspuren auf dem Ladecase"),
        SCRATCHES_ON_THE_LOADINGCASE("Kratzer auf dem Ladecase"),
        USETRACES_ON_THE_WRISTBAND("Gebrauchsspuren auf dem Armband"),
        USETRACES_ON_THE_AIRPODS("Gebrauchsspuren auf den Airpods"),
        SCRATCHES_ON_THE_AIRPODS("Kratzer auf den Airpods"),
        SCRATCHES_POWER_SUPPLY("Kratzer auf dem Netzteil"),
        SCRATCHES_HINGE("Kratzer auf dem Scharnier"),
        USETRACES_CASE("Gebrauchsspuren auf dem Gehäuse"),
        USETRACES_ON_THE_FOOT("Gebrauchsspuren auf dem Fuß"),
        USETRACES_ON_DISPLAY_STAND("Gebrauchsspuren auf dem Displaybein"),
        USETRACES_POWER_SUPPLY("Gebrauchsspuren auf dem Netzteil"),
        BANGED_CASE_EDGES("Gehäuseecken angestoßen"),
        USETRACES_IN_THE_LOADINGCASE("Gebrauchsspuren im Ladecase"),
        SCRATCHES_ON_THE_DIGITAL_CROWN("Kratzer auf der Digital Crown");

        private final String note;

        private StaticComment(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

        /**
         * Returns all values as set.
         *
         * @return all values as set.
         */
        public static EnumSet<StaticComment> valueSet() {
            return EnumSet.allOf(StaticComment.class);
        }

        /**
         * Returns a Set of StaticComment filtered by the {@link ProductGroup} and the product name.
         * <p>
         * @param group the {@link ProductGroup}
         * @param name  an optional name of the Product. May be null.
         * @return a Set of StaticComment filtered.
         */
        public static EnumSet<StaticComment> valueSet(ProductGroup group, String name) {
            if ( group == null ) {
                return EnumSet.allOf(StaticComment.class);
            }
            switch (group) {
                case DESKTOP:
                    return EnumSet.of(SCRATCHES_CASE, USETRACES_CASE, DENT_CASE);
                case MONITOR:
                    return EnumSet.of(SCRATCHES_ON_THE_BACK, SCRATCHES_DISPLAY, SCRATCHES_DISPLAYCOVER, SCRATCHES_ON_FOOT,
                            SCRATCHES_ON_DISPLAY_STAND, SCRATCHES_POWER_SUPPLY, DISPLAY_PIXERROR, USETRACES_ON_THE_BACK,
                            USETRACES_DISPLAY, USETRACES_DISPLAYCOVER, USETRACES_ON_THE_FOOT, USETRACES_ON_DISPLAY_STAND,
                            USETRACES_POWER_SUPPLY);
                case TABLET_SMARTPHONE:
                    return EnumSet.of(SCRATCHES_ON_THE_BACK, SCRATCHES_DISPLAY, SCRATCHES_DISPLAYCOVER,
                            SCRATCHES_POWER_SUPPLY, FOREIGN_PARTICLES_BELOW_DISPLAY, DISPLAY_PIXERROR,
                            SCRATCHES_ON_COVER_EDGE, USETRACES_ON_THE_BACK, USETRACES_DISPLAY, USETRACES_DISPLAYCOVER,
                            USETRACES_POWER_SUPPLY, BANGED_CASE_EDGES);
                case NOTEBOOK:
                    return EnumSet.of(SCRATCHES_ON_CASE_BOTTOM_SIDE, SCRATCHES_DISPLAY, SCRATCHES_DISPLAYCOVER,
                            SCRATCHES_COVER, SCRATCHES_ON_COVER_EDGE, SCRATCHES_POWER_SUPPLY, SCRATCHES_BELOW_KEYBOARD,
                            SCRATCHES_ABOVE_KEYBOARD, SCRATCHES_KEYBOARD, SCRATCHES_ON_TOUCH, SCRATCHES_TOUCH,
                            SCRATCHES_HINGE, USED_KEABOARD, USETRACES_COVER, USETRACES_ON_CASE_BOTTOM_SIDE,
                            USETRACES_DISPLAYCOVER, USETRACES_KEYBOARD, USETRACES_ABOVE_KEYBOARD,
                            USETRACES_AROUND_KEYBOARD, USETRACES_AROUND_TOUCH, USED_TOUCHPAD,
                            FOREIGN_PARTICLES_BELOW_DISPLAY, DISPLAY_PIXERROR);
                case PROJECTOR:
                    return EnumSet.of(SCRATCHES_CASE, USETRACES_CASE, DENT_CASE);
                case MISC:
                    if ( name == null ) {
                        return EnumSet.allOf(StaticComment.class);
                    } else if ( name.contains("AirPods") ) {
                        return EnumSet.of(SCRATCHES_ON_THE_AIRPODS, SCRATCHES_ON_THE_LOADINGCASE, USETRACES_ON_THE_AIRPODS,
                                USETRACES_ON_THE_LOADINGCASE, USETRACES_IN_THE_LOADINGCASE);
                    } else if ( name.contains("HomePod") ) {
                        return EnumSet.of(SCRATCHES_CASE,USETRACES_CASE);
                    } else if ( name.contains("Watch") ) {
                        return EnumSet.of(SCRATCHES_ON_CASE_BOTTOM_SIDE,SCRATCHES_DISPLAY,SCRATCHES_DISPLAYCOVER,
                                SCRATCHES_ON_THE_DIGITAL_CROWN,USETRACES_ON_THE_WRISTBAND,USETRACES_ON_CASE_BOTTOM_SIDE,
                                USETRACES_DISPLAY,USETRACES_DISPLAYCOVER);
                    } else if ( name.contains("Keyboard") ) {
                        return EnumSet.of(SCRATCHES_COVER,SCRATCHES_ON_CASE_BOTTOM_SIDE,SCRATCHES_KEYBOARD,
                                USETRACES_COVER,USETRACES_ON_CASE_BOTTOM_SIDE,USETRACES_KEYBOARD);
                    } else if ( name.contains("Apple TV") ) {
                        return EnumSet.of(SCRATCHES_CASE,USETRACES_CASE);
                    }
                default:
            }
            return EnumSet.allOf(StaticComment.class);
        }
    }

    /**
     * Most common internal notes that a unit may have.
     */
    public static enum StaticInternalComment implements INoteModel {

        /**
         * Prepared for Shipping ("versandfertig").
         */
        PREPARED_SHIPMENT("Versandfertig"),
        D_PARTITION_CHECKED("D Partition geprüft"),
        CLEANED("Gerät gereinigt"),
        RECOVERT("Gerät recovert"),
        REMOVED_OS("OS gelöscht"),
        EXTERNAL_GPU_SEEN("Externe Grafikkarte gesehen"),
        HDD_CASE("Festplattenhalterungen gesehen"),
        CHANGED_CONFIGURATION("geänderte Konfiguration"),
        REFILLED("Komponenten aufgefüllt"),
        DONGLE_INSIDE_MOUSE("Dongle in der Maus");

        private final String note;

        private StaticInternalComment(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

    }

    /**
     * Possible states every unit is categorized in.
     */
    public enum Condition implements INoteModel {

        AS_NEW("neuwertig"),
        ALMOST_NEW("nahezu neuwertig"),
        USED("gebraucht");

        private final String note;

        private Condition(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

    }

    /**
     * The possible identifiers for exactly one unit.
     */
    public enum Identifier {

        SERIAL, REFURBISHED_ID
    }

    public static enum Flag {

        PRICE_FIXED
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    @Version
    private short optLock;

    @OneToMany(cascade = ALL, mappedBy = "uniqueUnit", fetch = FetchType.EAGER)
    private Set<UniqueUnitHistory> history = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    private Map<Identifier, String> identifiers = new EnumMap<>(Identifier.class);

    // No Merge, Product may change while a UniqueUnit is detached.
    @NotNull
    @ManyToOne(cascade = {PERSIST, REFRESH, DETACH}, fetch = FetchType.EAGER)
    private Product product;

    @ElementCollection
    private Set<Equipment> equipments = EnumSet.noneOf(Equipment.class);

    /**
     * Represents Flags the user can set for this element. This is a better
     * idea, than creating multiple boolean values.
     */
    //TODO: There is only one flag so a single boolean would be okay - PP
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Flag> flags = EnumSet.noneOf(Flag.class);

    @ElementCollection
    private Set<StaticComment> comments = EnumSet.noneOf(StaticComment.class);

    @ElementCollection
    private Set<StaticInternalComment> internalComments = EnumSet.noneOf(StaticInternalComment.class);

    @NotNull
    @Column(name = "uniqueUnitCondition")
    private Condition condition;

    @NotNull
    private TradeName contractor;

    @Temporal(TemporalType.DATE)
    private Date mfgDate;

    @Basic
    @Lob
    @Column(length = 65536)
    private String comment;

    @Basic
    @Lob
    @Column(length = 65536)
    private String internalComment;

    private long shipmentId;

    private String shipmentLabel;

    /**
     * Id on the delivery notice to identify one item (raa number). 
     */
    private long receiveAssignAttribute = 0;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date inputDate;

    @NotNull
    private Warranty warranty = Warranty.ONE_YEAR_CARRY_IN;

    @Temporal(TemporalType.DATE)
    private Date warrentyValid;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    @SuppressWarnings("FieldMayBeFinal")
    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    @NotNull
    @OneToMany(cascade = ALL)
    @SuppressWarnings("FieldMayBeFinal")
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @NotNull
    @Basic(optional = false)
    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

    public UniqueUnit() {
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public int getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    public long getReceiveAssignAttribute() {
        return receiveAssignAttribute;
    }

    /**
     * Sets the value, converts null to empty strings.
     * 
     * @param receiveAssignAttribute
     */
    public void setReceiveAssignAttribute(long receiveAssignAttribute) {
        this.receiveAssignAttribute = receiveAssignAttribute;
    }
    
    public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public Warranty getWarranty() {
        return warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    public Date getWarrentyValid() {
        return warrentyValid;
    }

    public void setWarrentyValid(Date warrentyValid) {
        this.warrentyValid = warrentyValid;
    }

    public SalesChannel getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(SalesChannel salesChannel) {
        this.salesChannel = salesChannel;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public TradeName getContractor() {
        return contractor;
    }

    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInternalComment() {
        return internalComment;
    }

    public void setInternalComment(String internalComment) {
        this.internalComment = internalComment;
    }

    public long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Set<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<Equipment> equipments) {
        this.equipments = equipments;
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    public Set<StaticComment> getComments() {
        return comments;
    }

    public void setComments(Set<StaticComment> comments) {
        this.comments = comments;
    }

    public Set<StaticInternalComment> getInternalComments() {
        return internalComments;
    }

    public void setInternalComments(Set<StaticInternalComment> internalComments) {
        this.internalComments = internalComments;
    }

    public Product getProduct() {
        return product;
    }

    public String getShipmentLabel() {
        return shipmentLabel;
    }
    //</editor-fold>

    public void setPrice(PriceType type, double price, String comment) {
        price = TwoDigits.round(price);
        if ( TwoDigits.equals(getPrice(type), price) ) {
            return; // Don't set the same price
        }
        prices.put(type, price);
        priceHistories.add(new PriceHistory(type, price, new Date(), comment));
    }

    public boolean hasPrice(PriceType type) {
        return prices.get(type) != null && prices.get(type) > 0.01;
    }

    public double getPrice(PriceType type) {
        return prices.get(type) == null ? 0 : prices.get(type);
    }

    public Map<PriceType, Double> getPrices() {
        return Collections.unmodifiableMap(prices);
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistories;
    }

    /**
     * A non Productive Constructor.
     * <p/>
     * @param id
     */
    UniqueUnit(int id) {
        this.id = id;
    }

    public UniqueUnit(int id, Date mfgDate, String comment) {
        this.id = id;
        this.mfgDate = mfgDate;
        this.comment = comment;
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UniqueUnit(Product product, Date mfgDate, String comment) {
        this.mfgDate = mfgDate;
        this.comment = comment;
        setProduct(product);
    }

    public UniqueUnit(Date mfgDate, String comment) {
        this.mfgDate = mfgDate;
        this.comment = comment;
    }

    public boolean removeFlag(Flag flag) {
        return flags.remove(flag);
    }

    public boolean addFlag(Flag flag) {
        return flags.add(flag);
    }

    public String getIdentifier(Identifier type) {
        return identifiers.get(type);
    }

    public String getRefurbishId() {
        return identifiers.get(Identifier.REFURBISHED_ID);
    }

    public String getSerial() {
        return identifiers.get(Identifier.SERIAL);
    }

    public Map<Identifier, String> getIdentifiers() {
        return Collections.unmodifiableMap(identifiers);
    }

    public void setIdentifier(final Identifier type, final String identifier) {
        if ( this.identifiers.containsKey(type) && Objects.equals(this.identifiers.get(type), identifier) ) {
            return;
        }
        addHistory(type + " set to " + identifier);
        this.identifiers.put(type, identifier);
    }

    public void setShipmentLabel(String shipmentLabel) {
        if ( Objects.equals(this.shipmentLabel, shipmentLabel) ) {
            return;
        }
        addHistory("Setting Shipment from " + this.shipmentLabel + " to " + shipmentLabel);
        this.shipmentLabel = shipmentLabel;
    }

    public Set<UniqueUnitHistory> getHistory() {
        return Collections.unmodifiableSet(history);
    }

    public void addHistory(UniqueUnitHistory uniqueUnitHistory) {
        uniqueUnitHistory.uniqueUnit = this;
        history.add(uniqueUnitHistory);
    }

    public void addHistory(String comment) {
        addHistory(new UniqueUnitHistory(comment));
    }

    public PicoUnit toPicoUnit() {
        return new PicoUnit(id, UniqueUnitFormater.toPositionName(this));
    }

    // TODO: @Past fails in integration test. It seams that hibernate validator 6 uses toInstant on SQL date.
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( mfgDate != null && mfgDate.after(new Date()) )
            return "MFGDate (" + mfgDate + ") must be in the past, but is in the future. Now=" + new Date();
        return null;
    }

    /**
     * Sets the {@link Product} in consideration of equalancy and bidirectional
     * behaviour.
     * <p>
     * @param product
     */
    @SuppressWarnings("null")
    public void setProduct(Product product) {
        if ( product == null && this.product == null ) {
            return;
        }
        if ( this.product != null && this.product.equals(product) ) {
            return;
        }
        if ( this.product != null ) {
            this.product.units.remove(this);
        }
        if ( product != null ) {
            product.units.add(this);
        }
        this.product = product;
    }

    @Override
    public String toString() {
        String productString = null;
        String formatedMfgDate = null;
        String formatedInputDate = null;
        if ( product != null ) {
            productString = "[" + product.getPartNo() + "]" + product.getTradeName() + " " + product.getName();
        }
        if ( mfgDate != null ) {
            formatedMfgDate = Utils.ISO_DATE.format(mfgDate);
        }
        if ( inputDate != null ) {
            formatedInputDate = MEDIUM.format(inputDate);
        }
        return "UniqueUnit{" + "id=" + id + ", identifiers=" + identifiers + ", product=" + productString
                + ", prices=" + getPrices() + ", equipments=" + equipments + ", flags=" + flags + ", comments=" + comments + ", internalComments=" + internalComments
                + ", condition=" + condition + ", receiveAssignAttribute=" + receiveAssignAttribute
                + ", contractor=" + contractor + ", mfgDate=" + formatedMfgDate + ", shipmentId=" + shipmentId + ", shipmentLabel=" + shipmentLabel
                + ", salesChannel=" + getPrices() + ", inputDate=" + formatedInputDate + ", warranty=" + warranty + ", comment=" + comment
                + ", internalComment=" + internalComment + '}';
    }

    public SimpleUniqueUnit toSimple() {
        return new eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit.Builder()
                .id(id)
                .productId(product.getId())
                .refurbishedId(getRefurbishId())
                .contractor(getContractor())
                .shortDescription(UniqueUnitFormater.toPositionName(this))
                .detailedDiscription(UniqueUnitFormater.toDetailedDiscriptionLine(this))
                .build();
    }

    // TODO: Remove and extend BaseEntity if DWOSS-323 is solved
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final UniqueUnit other = (UniqueUnit)obj;
        if ( this.id != other.id ) return false;
        return true;
    }

    /**
     * Calls all m-n Relations recursive to ensure, that this instance works
     * detached.
     */
    // TODO: think about unitcollection in fetcheager.
    @Override
    public void fetchEager() {
        if ( getProduct() != null ) getProduct().getName();
        getComments().size();
        getInternalComments().size();
        getEquipments().size();
        getHistory().size();
        getPriceHistory().size();
    }
}
