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
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

import static javax.persistence.CascadeType.*;

/**
 * @composed 1 group n ProductGroup
 * @composed 1 brand n TradeName
 * @author oliver.guenther
 */
@Entity
@NamedQuery(name = "ProductSeries.byBrandGroupName", query = "select s from ProductSeries s where s.brand = ?1 and s.group = ?2 and s.name = ?3")
@SuppressWarnings("PersistenceUnitPresent")
public class ProductSeries extends BaseEntity implements Serializable, INamed {

    @XmlTransient
    @Id
    @GeneratedValue
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @XmlAttribute
    @NotNull
    @Basic(optional = false)
    @Column(name = "productGroup")
    private ProductGroup group;

    @XmlAttribute
    @NotNull
    @Basic(optional = false)
    private TradeName brand;

    @XmlAttribute
    @Basic(optional = false)
    private String name;

    @XmlTransient
    @NotNull
    @OneToMany(cascade = {DETACH, REFRESH, PERSIST}, mappedBy = "series", fetch = FetchType.EAGER)
    Set<ProductFamily> familys = new HashSet<>();

    @XmlAttribute
    @Column(columnDefinition = "DECIMAL(7,2)")
    private Double economicValue;

    public ProductSeries() {
    }

    /**
     * Non Productive Construcor
     *
     * @param id the database id, normally auto generated
     */
    ProductSeries(long id) {
        LoggerFactory.getLogger(ProductSpec.class).error("Usage of " + this.getClass().getName() + " id constructor. Will fail in a productive system");
        this.id = id;
    }

    public ProductSeries(TradeName brand, ProductGroup group, String name) {
        this.group = group;
        this.brand = brand;
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    public void addFamily(ProductFamily family) {
        if ( family == null ) return;
        family.setSeries(this);
    }

    public void removeFamily(ProductFamily family) {
        if ( family == null ) return;
        family.setSeries(null);
    }

    public Set<ProductFamily> getFamilys() {
        return Collections.unmodifiableSet(familys);
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

    public TradeName getBrand() {
        return brand;
    }

    public void setBrand(TradeName brand) {
        this.brand = brand;
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

    /**
     * Instance Validation. Validates: value brand is a brand.
     * <p>
     * @return null if valid, or a error message
     */
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( brand != null && !brand.isBrand() ) return brand.getName() + " is not a Brand";
        return null;
    }

    @Override
    public String toString() {
        return "ProductSeries{" + "id=" + id + ", group=" + group + ", brand=" + brand + ", name=" + name + ", economicValue=" + economicValue + '}';
    }
}
