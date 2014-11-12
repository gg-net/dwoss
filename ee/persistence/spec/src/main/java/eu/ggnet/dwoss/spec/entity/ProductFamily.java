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
import java.util.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import static javax.persistence.CascadeType.*;

/**
 * @composed 1 - n ProductSeries
 * @author oliver.guenther
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ProductFamily.byName", query = "select s from ProductFamily s where s.name = ?1"),
    @NamedQuery(name = "ProductFamily.byNameSeries", query = "select s from ProductFamily s where s.name = ?4 and s.series.brand = ?1 and s.series.group = ?2 and s.series.name = ?3")
})
public class ProductFamily implements Serializable, INamed {

    @XmlTransient
    @Id
    @GeneratedValue
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @XmlAttribute
    @Basic(optional = false)
    private String name;

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private ProductSeries series;

    @XmlTransient
    @NotNull
    @OneToMany(cascade = {DETACH, REFRESH, PERSIST}, mappedBy = "family", fetch = FetchType.EAGER)
    Set<ProductModel> models = new HashSet<>();

    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public ProductFamily() {
    }

    /**
     * Non Productive Constructor
     *
     * @param id the database id, normally auto generated.
     */
    ProductFamily(long id) {
        this.id = id;
    }

    public ProductFamily(String name) {
        this.name = name;
    }

    public ProductFamily(String name, ProductSeries series) {
        this.name = name;
        setSeries(series);
    }

    public long getId() {
        return id;
    }

    public ProductSeries getSeries() {
        return series;
    }

    public void setSeries(ProductSeries series) {
        if ( series == null && this.series == null ) return;
        if ( this.series != null && this.series == series ) return;
        if ( this.series != null ) this.series.familys.remove(this);
        if ( series != null ) series.familys.add(this);
        this.series = series;
    }

    public void addModel(ProductModel model) {
        if ( model == null ) return;
        model.setFamily(this);
    }

    public void removeModel(ProductModel model) {
        if ( model == null ) return;
        model.setFamily(null);
    }

    public Set<ProductModel> getModels() {
        return Collections.unmodifiableSet(models);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getEconomicValue() {
        return economicValue;
    }

    public void setEconomicValue(Double economicValue) {
        this.economicValue = economicValue;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ProductFamily other = (ProductFamily)obj;
        if ( this.id != other.id ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "ProductFamily{" + "id=" + id + ", series=" + series + ", name=" + name + ", economicValue=" + economicValue + '}';
    }

}
