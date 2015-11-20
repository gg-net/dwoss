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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.util.INoteModel;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

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
@NamedQueries({
    @NamedQuery(name = "ProductSpec.byPartNo", query = "select s from ProductSpec s where s.partNo = ?1"),
    @NamedQuery(name = "ProductSpec.byProductId", query = "select s from ProductSpec s where s.productId = ?1"),
    @NamedQuery(name = "ProductSpec.byProductIds", query = "SELECT s FROM ProductSpec s WHERE s.productId IN (?1)")
})
@EqualsAndHashCode(of = "id")
@ToString
public abstract class ProductSpec implements Serializable, EagerAble {

    @RequiredArgsConstructor
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
        USB_TYPE_C("USB Typ-C");

        @Getter
        private final String note;

        @Getter
        private final double economicValue;

        private Extra(String note) {
            this(note, 0);
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
    @Getter
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH}, optional = false)
    @Getter
    private ProductModel model;

    /**
     * PartNo of the Manufacturer.
     */
    @Column(unique = true)
    @Basic(optional = false)
    @NotNull
    @XmlAttribute
    @Getter
    @Setter
    private String partNo;

    /**
     * Optional uniqueunit.Product.id.
     * <p>
     * This is the weak reference to unqiueunit.Product
     */
    @Getter
    @Setter
    @XmlTransient
    @Column(unique = true)
    private Long productId;

    @Getter
    @Setter
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
    ProductSpec(long id) {
        this.id = id;
    }

    public ProductSpec(String partNo, Long productId) {
        this.partNo = partNo;
        this.productId = productId;
    }

    public Set<Extra> getDefaultExtras() {
        return EnumSet.allOf(Extra.class);
    }

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
    @Null
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
}
