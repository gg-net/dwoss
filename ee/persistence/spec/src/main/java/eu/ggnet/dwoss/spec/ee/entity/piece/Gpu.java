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
package eu.ggnet.dwoss.spec.ee.entity.piece;

import java.io.Serializable;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.INoteModel;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

import static eu.ggnet.dwoss.spec.ee.entity.piece.Gpu.Series.*;

/**
 *
 * @author bastian.venz
 */
@Entity
@NamedQuery(name = "Gpu.bySeriesModel", query = "select d from Gpu d where d.series = ?1 and d.model = ?2")
@SuppressWarnings("PersistenceUnitPresent")
public class Gpu extends BaseEntity implements Serializable {

    /**
     * A enum class with the names of the factory of the graphics card.
     */
    public static enum Manufacturer implements INoteModel {

        /**
         * Manufacturer AMD.
         */
        AMD("AMD", Arrays.asList(RADEON_HD_3000, RADEON_HD_4000, RADEON_HD_5000, RADEON_HD_6000, RADEON_HD_7000, RADEON_HD_8000, R7, R9, R4, R5, R2, R6, R8, R3, A8)),
        /**
         * Manufacturer Intel.
         */
        INTEL("Intel", Arrays.asList(INTEL_GRAPHICS)),
        /**
         * Manufacturer nVidia.
         */
        NVIDIA("nVidia", Arrays.asList(GEFORCE_ULP, GEFORCE_8000, GEFORCE_9000, GEFORCE_100, GEFORCE_200, GEFORCE_300, GEFORCE_400,
                GEFORCE_500, GEFORCE_600, GEFORCE_700, GEFORCE_800, QUADRO_2000, QUADRO_600, GEFORCE_900, QUADRO_4000, GEFORCE_10, GEFORCE_TITAN, GEFORCE_MX, GEFORCE_RTX)),
        /**
         * Manufacturer Apple.
         */
        APPLE("Apple", Arrays.asList(APPLE_A));

        private final List<Series> series;

        private String note;

        private Manufacturer(String note, List<Series> series) {
            this.note = note;
            this.series = series;
        }

        public List<Series> getSeries() {
            return series;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    /**
     * A enum class with the types of a graphics card.
     */
    @XmlType(name = "GpuType")
    public enum Type {

        MOBILE, DESKTOP
    }

    @XmlType(name = "GpuSeries")
    public enum Series implements eu.ggnet.dwoss.core.common.INoteModel {

        GEFORCE_100("GeForce 100 Series"),
        GEFORCE_200("GeForce 200 Series"),
        GEFORCE_300("GeForce 300 Series"),
        GEFORCE_400("GeForce 400 Series"),
        GEFORCE_500("GeForce 500 Series"),
        GEFORCE_600("GeForce 600 Series"),
        RADEON_HD_4000("Radeon HD 4000 Series"),
        RADEON_HD_5000("Radeon HD 5000 Series"),
        RADEON_HD_6000("Radeon HD 6000 Series"),
        INTEL_GRAPHICS("Graphics Series"),
        GEFORCE_8000("GeForce 8000 Series"),
        GEFORCE_9000("GeForce 9000 Series"),
        RADEON_HD_7000("Radeon HD 7000 Series"),
        RADEON_HD_3000("Radeon HD 3000 Series"),
        GEFORCE_ULP("ULP"),
        QUADRO_2000("Quadro 2000"),
        GEFORCE_700("GeForce 700 Series"),
        APPLE_A("A Series"),
        RADEON_HD_8000("Radeon HD 8000 Series"),
        QUADRO_600("Quadro 600"),
        GEFORCE_800("GeForce 800 Series"),
        R7("Radeon R7"),
        R9("Radeon R9"),
        R4("Radeon R4"),
        R5("Radeon R5"),
        R2("Radeon R2"),
        R6("Radeon R6"),
        GEFORCE_900("GeForce 900 Series"),
        QUADRO_4000("Quadro 4000"),
        R8("Radeon R8"),
        R3("Radeon R3"),
        A8("Radeon A8"),
        GEFORCE_TITAN("GeForce Titan Series"),
        GEFORCE_10("GeForce 10 Series"),
        GEFORCE_MX("GeForce MX Series"),
        GEFORCE_RTX("GeForce RTX Series");

        String note;

        private Series(String note) {
            this.note = note;
        }

        @Override
        public String getNote() {
            return note;
        }

        public Manufacturer getManufacturer() {
            for (Manufacturer manufacturer : Manufacturer.values()) {
                if ( manufacturer.getSeries().contains(this) ) return manufacturer;
            }
            throw new RuntimeException(this + " has no Manufacturer assoziated !");
        }
    }

    @XmlTransient
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    /**
     * The model (productModel, productNumber of the Manufacturer).
     */
    @XmlAttribute
    @NotNull
    @Pattern(regexp = "(\\S.*\\S){1,250}")
    private String model;

    /**
     * An optional Name, which may be use in replacement of Series + Model.
     */
    @XmlAttribute
    private String name;

    /**
     * The type of the graphics card.
     */
    @XmlElement(name = "type")
    @XmlElementWrapper
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Type> types = EnumSet.noneOf(Type.class);

    @XmlAttribute
    @NotNull
    private Series series;

    /**
     *
     * Not used anymore, can be removed later.
     *
     * @deprecated
     */
    @Deprecated
    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public Gpu() {
    }

    public Gpu(Gpu.Series series, Set<Gpu.Type> types, String model) {
        this.series = series;
        this.types = types;
        this.model = model;
    }

    public Gpu(Type type, Series series, String model) {
        this.model = model;
        this.series = series;
        this.types = EnumSet.of(type);
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }

    public Manufacturer getManufacturer() {
        return series.getManufacturer();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public void addType(Type type) {
        types.add(type);
    }

    public void removeType(Type type) {
        types.remove(type);
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void setTypes(Set<Type> types) {
        this.types = types;
    }

    public Double getEconomicValue() {
        return economicValue;
    }

    public void setEconomicValue(Double economicValue) {
        this.economicValue = economicValue;
    }
    //</editor-fold>

    public boolean equalsContent(Gpu other) {
        if ( other == null ) return false;
        if ( (this.model == null) ? (other.model != null) : !this.model.equals(other.model) ) return false;
        if ( (this.name == null) ? (other.name != null) : !this.name.equals(other.name) ) return false;
        if ( this.types != other.types && (this.types == null || !this.types.equals(other.types)) ) return false;
        if ( this.series != other.series ) return false;
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
