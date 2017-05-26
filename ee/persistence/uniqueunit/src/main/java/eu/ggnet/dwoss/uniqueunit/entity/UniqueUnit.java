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
package eu.ggnet.dwoss.uniqueunit.entity;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.*;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Equipment.*;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static javax.persistence.CascadeType.*;

/**
 * A UniqueUnit represents exactly one unit that is unique in the whole system.
 * Identefiable through {@link Identifier}
 *
 * @has n - 1 Product
 * @has 1 - n UniqueUnitHistory
 * @has n - m UniqueUnit.Equipment
 * @has n - m UniqueUnit.StaticComment
 * @has n - m UniqueUnit.StaticInternalComment
 * @has n - 1 UniqueUnit.Condition
 * @has 1 - n PriceHistory
 * @has 1 - 1 Identifier
 * @has n - m Flag
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@NamedQueries({
    // HINT: Netbeans Warinig is wrong. 2. HINT: Hibernate builds defective Query.
    @NamedQuery(name = "UnqiueUnit.findByIdenfiersTypeValue", query = "SELECT u FROM UniqueUnit u join u.identifiers i WHERE KEY(i) = ?1 and VALUE(i) IN (?2)"),
    @NamedQuery(name = "UnqiueUnit.betweenInputDates", query = "SELECT u FROM UniqueUnit u WHERE u.inputDate >= ?1 AND u.inputDate <= ?2"),
    @NamedQuery(name = "UnqiueUnit.betweenInputDatesAndContractor", query = "SELECT u FROM UniqueUnit u WHERE u.inputDate >= ?1 AND u.inputDate <= ?2 and u.contractor = ?3"),
    @NamedQuery(name = "UniqueUnit.findByIds", query = "SELECT u FROM UniqueUnit u WHERE u.id IN (:idList)"),
    @NamedQuery(name = "UniqueUnit.byProductPartNo", query = "SELECT u FROM UniqueUnit u WHERE u.product.partNo = ?1"),
    @NamedQuery(name = "UniqueUnit.byProductPartNosInputDate", query = "SELECT u FROM UniqueUnit u WHERE u.product.partNo in (?1) AND u.inputDate >= ?2 AND u.inputDate <= ?3"),
    @NamedQuery(name = "UnqiueUnit.byContractor", query = "SELECT u FROM UniqueUnit u WHERE u.contractor = ?1"),
    @NamedQuery(name = "UniqueUnit.countByInputDateContractor",
                query = "select new eu.ggnet.dwoss.uniqueunit.eao.CountHolder(u.inputDate, u.product.tradeName, u.contractor, count(u.id)) "
                + "from UniqueUnit u where u.inputDate >= :start and u.inputDate <= :end GROUP BY u.contractor, u.product.tradeName, cast(u.inputDate as date)")
})
public class UniqueUnit implements Serializable, EagerAble {

    public static NavigableMap<String, UniqueUnit> asMapByRefurbishId(Collection<UniqueUnit> uus) {
        NavigableMap<String, UniqueUnit> result = new TreeMap<>();
        for (UniqueUnit uu : uus) {
            result.put(uu.getIdentifier(REFURBISHED_ID), uu);
        }
        return result;
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
        SPARE_STRAP("Ersatzarmband");

        private final String note;

        private final double economicValue;

        private Equipment(String note) {
            this(note, 0);
        }

        private Equipment(String note, double economicValue) {
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

        public static Set<Equipment> getEquipments() {
            return getEquipments(null);
        }

        /**
         * Returns a Set of Equipment filtered by the {@link ProductGroup}.
         * <p/>
         * @param group the {@link ProductGroup}
         * @return a Set of Equipment filtered by the {@link ProductGroup}.
         */
        public static Set<Equipment> getEquipments(ProductGroup group) {
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
                    Set<Equipment> equipments = getEquipments(ProductGroup.DESKTOP);
                    equipments.addAll(getEquipments(ProductGroup.MONITOR));
                    return equipments;
                case SERVER:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, MOUSE, KEYBOARD, MANUAL, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE);
                case DESKTOP:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, MOUSE, KEYBOARD, GAME_MOUSE, GAME_KEYBOARD, CABLELES_KEYBOARD, MANUAL, ALTERNATIBVE_USB_CABLE,
                            CABLELES_MOUSE, ANTENNA_ADAPTER, DONGLE, AC_ADAPTER_INC_CABLE, FOOT, WLAN_KINK_ANTENNA, DVI_HDMI_ADAPTOR, ALTERNATIVE_PLUGIN_AC_ADAPTER);
                case MONITOR:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, POWER_CABLE, AC_ADAPTER_INC_CABLE, VGA_CABLE, DVI_CABLE, HDMI_CABLE, FOOT,
                            DISPLAY_FOOT_CONNECTOR, THREE_D_GLASSES, USB_KABEL, MANUAL, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE, DISPLAYPORT_CABLE);
                case TABLET_SMARTPHONE:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER, AC_ADAPTER_INC_CABLE, BATTERY, USB_KABEL, KEYBOARD_DOCK, VGA_NETWORK_ADAPTER, HEADSET, MANUAL,
                            MICRO_USB_TO_USB_ADAPTER, PORTFOLIO_CASE, SDCARD_16GB, HDMI_VGA_ADAPTER, USB_NETWORK_ADAPTER, PORTFOLIO_CASE, USB_MICRO_HDMI_ADAPTER, SIM_OPENER,
                            PORTFOLIO_CASE_INTEGRATED_KEYBOARD, CRUNCHCOVER, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE, MS_OFFICE_365_PERSONAL,
                            MS_OFFICE_HOME_AND_STUDENT_2013, STYLUS,MAGNETIC_CHARGING_CABLE,SPARE_STRAP);
                case NOTEBOOK:
                    return EnumSet.of(ORIGINAL_BOXED, ALTERNATIVE_BOXED, PLUGIN_AC_ADAPTER, AC_ADAPTER_INC_CABLE, BATTERY, REMOTE, EXT_ANTENNA, THREE_D_GLASSES, MANUAL, DONGLE,
                            VGA_NETWORK_ADAPTER, CABLELES_MOUSE, HDMI_VGA_ADAPTER, USB_NETWORK_ADAPTER, PORTFOLIO_CASE, VGA_USB_NETWORK_ADAPTER, SIM_OPENER,
                            PORTFOLIO_CASE_INTEGRATED_KEYBOARD, ALTERNATIVE_PLUGIN_AC_ADAPTER, ALTERNATIBVE_USB_CABLE, KEYBOARD_DOCK, MS_OFFICE_365_PERSONAL,
                            MS_OFFICE_HOME_AND_STUDENT_2013, STYLUS, USB_KABEL, VGA_USB_ADAPTER,MAGNETIC_CHARGING_CABLE);
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
        USETRACES_TOUCH("Gebrauchsspuren um die Tastatur"),
        USETRACES_KEYBOARD("Gebrauchsspuren um das Touchpad"),
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
        FOOT_OR_STAND_MISSING("Fuß/Bein fehlt");

        private final String note;

        private final double economicValue;

        private StaticComment(String note) {
            this(note, 0);
        }

        private StaticComment(String note, double economicValue) {
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

        private final double economicValue;

        private StaticInternalComment(String note) {
            this(note, 0);
        }

        private StaticInternalComment(String note, double economicValue) {
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
    }

    /**
     * Possible states every unit is categorized in.
     */
    public enum Condition implements INoteModel {

        AS_NEW("neuwertig"),
        ALMOST_NEW("nahezu neuwertig"),
        USED("gebraucht");

        private final String note;

        private final double economicValue;

        private Condition(String note) {
            this(note, 0);
        }

        private Condition(String note, double economicValue) {
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

    /**
     * Classification inspired by Also.
     * <p/>
     * @deprecated Cooperation with Also has been canceled, may be removed.
     */
    @RequiredArgsConstructor
    @Getter
    @Deprecated
    public static enum BnClassification {

        B0_UNDEFINED("B0 - Keine Klassifikation"),
        B1_NEW("B1 - Vergleichbar mit Neuware"),
        B2_AS_NEW("B2 - Neuwertig und Vollständig"),
        B3_USED("B3 - Gebraucht und Verkaufsfähig"),
        B4_USED("B4 - Gebraucht und Unvollständig"),
        B5_BROCKEN("B5 - Beschädigt"),
        B6_CHEAP("B6 - Wert unter 25,- €");

        private final String name;

    }

    @Getter
    @Id
    @GeneratedValue
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

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    @NotNull
    @OneToMany(cascade = ALL)
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @Getter
    @Setter
    @ElementCollection
    private Set<Equipment> equipments = EnumSet.noneOf(Equipment.class);

    /**
     * Represents Flags the user can set for this element. This is a better
     * idea, than creating multiple boolean values.
     */
    //TODO: There is only one flag so a single boolean would be okay - PP
    @Getter
    @Setter
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Flag> flags = EnumSet.noneOf(Flag.class);

    @Getter
    @Setter
    @ElementCollection
    private Set<StaticComment> comments = EnumSet.noneOf(StaticComment.class);

    @Getter
    @Setter
    @ElementCollection
    private Set<StaticInternalComment> internalComments = EnumSet.noneOf(StaticInternalComment.class);

    @Getter
    @Setter
    @NotNull
    @Column(name = "uniqueUnitCondition")
    private Condition condition;

    @NotNull
    private TradeName contractor;

    public TradeName getContractor() {
        return contractor;
    }

    public void setContractor(TradeName contractor) {
        this.contractor = contractor;
    }

    @Getter
    @Setter
    @Past
    @Temporal(TemporalType.DATE)
    private Date mfgDate;

    @Getter
    @Setter
    @Basic
    @Lob
    @Column(length = 65536)
    private String comment;

    @Getter
    @Setter
    @Basic
    @Lob
    @Column(length = 65536)
    private String internalComment;

    @Getter
    @Setter
    private long shipmentId;

    @Getter
    private String shipmentLabel;

    @Getter
    @Setter
    @NotNull
    @Basic(optional = false)
    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date inputDate;

    @Getter
    @Setter
    @NotNull
    private Warranty warranty = Warranty.ONE_YEAR_CARRY_IN;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date warrentyValid;

    /**
     * Also Classifiaction.
     * <p/>
     * @deprecated Cooperation with Also has been canceled, may be removed.
     */
    @Getter
    @Setter
    @NotNull
    @Deprecated
    private BnClassification classification = BnClassification.B0_UNDEFINED;

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

    public void setPrice(PriceType type, double price, String comment) {
        if ( MathUtil.equals(getPrice(type), price) ) {
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

    public boolean removeFlag(Flag flag) {
        return flags.remove(flag);
    }

    public boolean addFlag(Flag flag) {
        return flags.add(flag);
    }

    public List<PriceHistory> getPriceHistory() {
        return priceHistories;
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
        addHistory(UniqueUnitHistory.Type.UNIQUE_UNIT, type + " set to " + identifier);
        this.identifiers.put(type, identifier);
    }

    public void setShipmentLabel(String shipmentLabel) {
        if ( Objects.equals(this.shipmentLabel, shipmentLabel) ) {
            return;
        }
        addHistory(UniqueUnitHistory.Type.UNIQUE_UNIT, "Setting Shipment from " + this.shipmentLabel + " to " + shipmentLabel);
        this.shipmentLabel = shipmentLabel;
    }

    public Set<UniqueUnitHistory> getHistory() {
        return Collections.unmodifiableSet(history);
    }

    public void addHistory(UniqueUnitHistory uniqueUnitHistory) {
        uniqueUnitHistory.uniqueUnit = this;
        history.add(uniqueUnitHistory);
    }

    public void addHistory(UniqueUnitHistory.Type type, String comment) {
        addHistory(new UniqueUnitHistory(type, comment));
    }

    public void addHistory(String comment) {
        addHistory(new UniqueUnitHistory(UniqueUnitHistory.Type.UNDEFINED, comment));
    }

    public Product getProduct() {
        return product;
    }

    /**
     * Sets the {@link Product} in consideration of equalancy and bidirectional
     * behaviour.
     * <p/>
     * @param product
     */
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
            formatedMfgDate = DateFormats.ISO.format(mfgDate);
        }
        if ( inputDate != null ) {
            formatedInputDate = MEDIUM.format(inputDate);
        }
        return "UniqueUnit{" + "id=" + id + ", identifiers=" + identifiers + ", product=" + productString + ", prices=" + prices + ", equipments=" + equipments
                + ", flags=" + flags + ", comments=" + comments + ", internalComments=" + internalComments + ", condition=" + condition
                + ", contractor=" + contractor + ", mfgDate=" + formatedMfgDate + ", shipmentId=" + shipmentId + ", shipmentLabel=" + shipmentLabel
                + ", salesChannel=" + salesChannel + ", inputDate=" + formatedInputDate + ", warranty=" + warranty + ", classification=" + classification
                + ", comment=" + comment + ", internalComment=" + internalComment + '}';
    }

    /**
     * Calls all m-n Relations recursive to ensure, that this instance works
     * detached.
     */
    @Override
    public void fetchEager() {
        if ( getProduct() != null ) {
            getProduct().getName();
        }
        getComments().size();
        getInternalComments().size();
        getEquipments().size();
        getHistory().size();
        getPriceHistory().size();
    }
}
