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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.INoteModel;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;

import static javax.persistence.CascadeType.*;

/**
 * The minimal abstract information about a ProductSpec.
 * <p>
 * A ProductSpec has a weak reference to uniqueunit.Product.
 * <p>
 * @composed 1 - n ProductModel
 * @author oliver.guenther
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "ProductSpec.byPartNo", query = "select s from ProductSpec s where s.partNo = ?1")
@NamedQuery(name = "ProductSpec.byProductId", query = "select s from ProductSpec s where s.productId = ?1")
@NamedQuery(name = "ProductSpec.byProductIds", query = "SELECT s FROM ProductSpec s WHERE s.productId IN (?1)")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class ProductSpec extends BaseEntity implements Serializable, EagerAble {

    public static enum Extra implements INoteModel {

        USB_3("USB 3"),
        CARD_READER("Kartenleser"),
        WLAN_TO_G("WLAN b + g"),
        WLAN_TO_N("WLAN b + g + n"),
        UMTS("UMTS (3G)", 30.),
        KAMERA("Webcam"),
        BLUETOOTH("Bluetooth"),
        FINGER_SCANNER("Fingerabdruck Scanner"),
        PS_2("PS2"),
        E_SATA("eSATA"),
        THREE_D("3D fähig"),
        PIVOT("Pivot Funktion"),
        HIGHT_CHANGEABLE("höhenverstellbar"),
        SPEAKERS("Lautsprecher"),
        TOUCH("Touchscreen"),
        PENSLOT("Stifthalterung"),
        CONVERTABLE("Convertable (Display drehbar)"),
        BATTERY_INTEGRATED("Akku integriert"),
        TV_TUNER("TV Tuner"),
        INFRARED_RESCEIVER("Infrarot Empfänger"),
        DUAL_DISPLAY_TABLET("Dual Display Tablet"),
        MEDIA_STATION("Multimedia Wohnzimmer PC"),
        LTE("LTE (4G)", 30.),
        DUAL_LOAD("Dual Load"),
        THUNDERBOLT("Thunderbolt"),
        COVER("Cover"),
        GORILLA_GLASS("Gorilla Glass"),
        KEYBOARD_BACKGROUND_LIGHT("beleuchtete Tastatur"),
        DUAL_SIM("Dual-SIM"),
        LIGHTNING("Lightning"),
        WLAN_AC("WLAN ac"),
        IPS_DISPLAY("IPS-Display"),
        USB_TYPE_C("USB Typ-C"),
        THREE_SIXTY_DEGREE_TORQUE_HINGE("360° Dual-Torque-Hinge"),
        VGA_ADAPTER("VGA-Adapter"),
        MAGSAFE_POWER_ADAPTER("MagSafe Power Adapter"),
        REPLACEMENT_STRAP("Ersatzarmband"),
        BLUE_LIGHT_FILTER("Blaulichtfilter"),
        REALSENSE_3D_CAM("RealSense 3D Kamera"),
        ITEGATED_SIM("Integrierte Sim"),
        WLAN_AX("WLAN ax"),
        DISPLAY_144("144 Hz Display"),
        DISPLAY_120("120 Hz Display");

        private final String note;

        private final double economicValue;

        private Extra(String note, double economicValue) {
            this.note = note;
            this.economicValue = economicValue;
        }

        private Extra(String note) {
            this(note, 0);
        }

        @Override
        public String getNote() {
            return note;
        }

        public double getEconomicValue() {
            return economicValue;
        }

    }

    public static ProductSpec newInstance(ProductGroup group) {
        switch (group) {
            case DESKTOP:
            case SERVER:
                return new Desktop();
            case DESKTOP_BUNDLE:
                return new DesktopBundle();
            case ALL_IN_ONE:
                return new AllInOne();
            case TABLET_SMARTPHONE:
                return new Tablet();
            case MONITOR:
            case TV:
                return new Monitor();
            case NOTEBOOK:
                return new Notebook();
            case MISC:
            case PROJECTOR:
            case PHONE:
                return new BasicSpec();
            case COMMENTARY:
            default:
        }
        throw new RuntimeException(group + " not supported. Think about");
    }

    @XmlTransient
    @Id
    @GeneratedValue
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH}, optional = false)
    private ProductModel model;

    /**
     * PartNo of the Manufacturer.
     */
    @Column(unique = true)
    @Basic(optional = false)
    @NotNull
    @XmlAttribute
    private String partNo;

    /**
     * Optional uniqueunit.Product.id.
     * <p>
     * This is the weak reference to unqiueunit.Product
     */
    @XmlTransient
    @Column(unique = true)
    private Long productId;

    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public ProductSpec() {
    }

    /**
     * Non Productive Constructor
     * <p>
     * @param id the database id, normally auto generated.
     */
    public ProductSpec(long id) {
        LoggerFactory.getLogger(ProductSpec.class).error("Usage of " + this.getClass().getName() + " id constructor. Will fail in a productive system");
        this.id = id;
    }

    public ProductSpec(String partNo, Long productId) {
        this.partNo = partNo;
        this.productId = productId;
    }

    public Set<Extra> getDefaultExtras() {
        return EnumSet.allOf(Extra.class);
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }

    public ProductModel getModel() {
        return model;
    }

    public String getPartNo() {
        return partNo;
    }

    public Long getProductId() {
        return productId;
    }

    public Double getEconomicValue() {
        return economicValue;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setEconomicValue(Double economicValue) {
        this.economicValue = economicValue;
    }

    //</editor-fold>
    public void setModel(ProductModel model) {
        if ( model == null && this.model == null ) return;
        if ( this.model != null && this.model == model ) return;
        if ( this.model != null ) this.model.specs.remove(this);
        if ( model != null ) model.specs.add(this);
        this.model = model;
    }

    @Override
    public void fetchEager() {
        if ( getModel() != null ) getModel().fetchEager();
    }

    /**
     * Returns null if the instance is valid, or a string representing the error.
     * <p>
     * @return null if the instance is valid, or a string representing the error.
     */
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( model == null
                || model.getFamily() == null
                || model.getFamily().getSeries() == null
                || model.getFamily().getSeries().getBrand() == null
                || model.getFamily().getSeries().getBrand().getManufacturer().getPartNoSupport() == null )
            return null;
        return model.getFamily().getSeries().getBrand().getManufacturer().getPartNoSupport().violationMessages(partNo);
    }

    // TODO: Please realize this through Validation Groups.
    @PrePersist
    @PreUpdate
    private void prePersitValidate() {
        if ( model == null ) throw new RuntimeException("Model is null");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
