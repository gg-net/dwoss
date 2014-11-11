/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.spec.entity.piece;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

import eu.ggnet.dwoss.util.INoteModel;

import static eu.ggnet.dwoss.spec.entity.piece.Cpu.Series.*;

/**
 * Represents a CPU of a Product.
 * <p>
 * @author bastian.venz
 */
@Entity
@NamedQuery(name = "Cpu.bySeriesModel", query = "select d from Cpu d where d.series = ?1 and d.model = ?2")
public class Cpu implements Serializable {

    /**
     * The Manufacturer of the CPU.
     */
    public static enum Manufacturer implements INoteModel {

        AMD("AMD", EnumSet.of(ATHLON, PHENOM, SEMPRON, TURION, OPTERON, AMD_A, AMD_C, AMD_E, AMD_V, AMD_FX, AMD_G)),
        INTEL("Intel", EnumSet.of(ATOM, CELERON, PENTIUM, CORE, CORE_I3, CORE_I5, CORE_I7, XEON)),
        NVIDIA("nVidia", EnumSet.of(TEGRA)),
        MEDIATEK("MediaTek", EnumSet.of(MT)),
        APPLE("Apple", EnumSet.of(APPLE_A));

        private final Set<Series> series;

        private String note;

        private Manufacturer(String note, Set<Series> series) {
            this.note = note;
            this.series = series;
        }

        public Set<Series> getSeries() {
            return series;
        }

        @Override
        public String getNote() {
            return note;
        }
    }

    /**
     * The Type of the CPU.
     */
    @XmlType(name = "CpuType")
    public static enum Type {

        DESKTOP, MOBILE;
    }

    /**
     * The Series of the CPU.
     */
    @XmlType(name = "CpuSeries")
    public static enum Series implements eu.ggnet.dwoss.util.INoteModel {

        ATOM("Atom"),
        CELERON("Celeron"),
        PENTIUM("Pentium"),
        CORE("Core/Core 2 Series"),
        CORE_I3("Core I3"),
        CORE_I5("Core I5"),
        CORE_I7("Core I7"),
        XEON("Xeon"),
        ATHLON("Athlon"),
        PHENOM("Phenom"),
        SEMPRON("Sempron"),
        TURION("Turion"),
        OPTERON("Opteron"),
        AMD_A("A Series"),
        AMD_C("C Series"),
        AMD_E("E Series"),
        AMD_V("V Series"),
        AMD_FX("FX Series"),
        TEGRA("Tegra"),
        MT("MT"),
        APPLE_A("A Series"),
        AMD_G("G Series");

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
    @GeneratedValue
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    /**
     * The model of the CPU.
     */
    @XmlAttribute
    @NotNull
    @Pattern(regexp = "(\\S.*\\S){1,250}")
    private String model;

    /**
     * The printable name of the CPU.
     * <p>
     * e.g. Data while receipt:
     * series=Core/Core2 Series, model=Q8500 -&gt; autogenerated name Core/Core2 Series Q8500, name by human=Core 2 Quad Q8500
     */
    @XmlAttribute
    private String name;

    /**
     * The Frequenzy of the CPU.
     */
    @XmlAttribute
    private Double frequency;

    /**
     * The Type of the CPU.
     */
    @XmlElement(name = "type")
    @XmlElementWrapper
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Type> types = EnumSet.noneOf(Type.class);

    /**
     * The Series of the CPU.
     */
    @XmlAttribute
    @NotNull
    private Series series;

    /**
     * Number of cores of the CPU
     */
    @XmlAttribute
    private Integer cores;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
    private Gpu embeddedGpu;

    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public Cpu() {
    }

    public Cpu(Series series, Set<Type> types, String model) {
        this.series = series;
        this.types = types;
        this.model = model;
    }

    public Cpu(Series series, String model, Type type, Double cpuFrequency, Integer cores) {
        this.model = model;
        this.series = series;
        this.frequency = cpuFrequency;
        this.types = EnumSet.of(type);
        this.cores = cores;
    }

    public long getId() {
        return id;
    }

    public Double getEconomicValue() {
        return economicValue;
    }

    public void setEconomicValue(Double economicValue) {
        this.economicValue = economicValue;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public Manufacturer getManufacturer() {
        return series.getManufacturer();
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void addType(Type type) {
        types.add(type);
    }

    public void removeType(Type type) {
        types.remove(type);
    }

    public void setTypes(Set<Type> types) {
        this.types = types;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Series getSeries() {
        return series;
    }

    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Gpu getEmbeddedGpu() {
        return embeddedGpu;
    }

    public void setEmbeddedGpu(Gpu embeddedGpu) {
        this.embeddedGpu = embeddedGpu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Cpu other = (Cpu)obj;
        if ( this.id != other.id ) return false;
        return true;
    }

    public boolean equalsContent(Cpu other) {
        if ( other == null ) return false;
        if ( (this.model == null) ? (other.model != null) : !this.model.equals(other.model) ) return false;
        if ( this.frequency != other.frequency && (this.frequency == null || !this.frequency.equals(other.frequency)) ) return false;
        if ( this.types != other.types && (this.types == null || !this.types.equals(other.types)) ) return false;
        if ( this.series != other.series ) return false;
        if ( (this.name == null) ? (other.name != null) : !this.name.equals(other.name) ) return false;
        if ( this.cores != other.cores && (this.cores == null || !this.cores.equals(other.cores)) ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Cpu{" + "id=" + id + ", model=" + model + ", name=" + name + ", frequency=" + frequency + ", types=" + types + ", series=" + series + ", cores=" + cores + ", embeddedGpu=" + embeddedGpu + ", economicValue=" + economicValue + '}';
    }
}
