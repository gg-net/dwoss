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

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;

import static jakarta.persistence.CascadeType.*;

/**
 *
 * @composed 1 - n ProductFamily
 * @author oliver.guenther
 */
@Entity
@NamedQuery(name = "ProductModel.byName", query = "select m from ProductModel m where m.name = ?1")
@NamedQuery(name = "ProductModel.byNameFamilySeries",
            query = "select m from ProductModel m where m.name = ?5 and m.family.name = ?4 "
            + "and m.family.series.brand = ?1 and m.family.series.group = ?2 and m.family.series.name = ?3")
@SuppressWarnings("PersistenceUnitPresent")
public class ProductModel extends BaseEntity implements Serializable, INamed, EagerAble {

    @XmlTransient
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
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
    private ProductFamily family;

    @NotNull
    @OneToMany(cascade = {DETACH, REFRESH}, mappedBy = "model")
    @XmlTransient
    Set<ProductSpec> specs = new HashSet<>();

    @Column(columnDefinition = "DECIMAL(7,2)")
    @XmlAttribute
    private Double economicValue;

    public ProductModel() {
    }

    public ProductModel(long id) {
        LoggerFactory.getLogger(ProductSpec.class).error("Usage of " + this.getClass().getName() + " id constructor. Will fail in a productive system");
        this.id = id;
    }

    public ProductModel(String name) {
        this.name = name;
    }

    public ProductModel(String name, ProductFamily family) {
        this.name = name;
        setFamily(family);
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
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
    public long getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    public ProductFamily getFamily() {
        return family;
    }
    //</editor-fold>

    public void setFamily(ProductFamily family) {
        if ( family == null && this.family == null ) return;
        if ( this.family != null && this.family == family ) return;
        if ( this.family != null ) this.family.models.remove(this);
        if ( family != null ) family.models.add(this);
        this.family = family;
    }

    public void addSpec(ProductSpec spec) {
        if ( spec == null ) return;
        spec.setModel(this);
    }

    public void removeSpec(ProductSpec spec) {
        if ( spec == null ) return;
        spec.setModel(null);
    }

    public Set<ProductSpec> getSpecs() {
        return Collections.unmodifiableSet(specs);
    }

    @Override
    public void fetchEager() {
        if ( getFamily() != null ) {
            getFamily().getModels().size();
            if ( getFamily().getSeries() != null ) {
                getFamily().getSeries().getFamilys().size();
            }
        }
        getSpecs().size();
    }

    @Override
    public String toString() {
        return "ProductModel{" + "id=" + id + ", name=" + name + ", family=" + family + ", economicValue=" + economicValue + '}';
    }
}
