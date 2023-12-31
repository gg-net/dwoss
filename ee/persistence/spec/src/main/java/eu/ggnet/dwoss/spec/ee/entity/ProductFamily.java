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
 * @composed 1 - n ProductSeries
 * @author oliver.guenther
 */
@Entity
@NamedQuery(name = "ProductFamily.byName", query = "select s from ProductFamily s where s.name = ?1")
@NamedQuery(name = "ProductFamily.byNameSeries", query = "select s from ProductFamily s where s.name = ?4 and s.series.brand = ?1 and s.series.group = ?2 and s.series.name = ?3")
@SuppressWarnings("PersistenceUnitPresent")
public class ProductFamily extends BaseEntity implements Serializable, INamed, EagerAble {

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
    private ProductSeries series;

    @XmlTransient
    @NotNull
    @OneToMany(cascade = {DETACH, REFRESH, PERSIST}, mappedBy = "family")
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
        LoggerFactory.getLogger(ProductSpec.class).error("Usage of " + this.getClass().getName() + " id constructor. Will fail in a productive system");
        this.id = id;
    }

    public ProductFamily(String name) {
        this.name = name;
    }

    public ProductFamily(String name, ProductSeries series) {
        this.name = name;
        setSeries(series);
    }

    @Override
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
    public String toString() {
        return "ProductFamily{" + "id=" + id + ", series=" + series + ", name=" + name + ", economicValue=" + economicValue + '}';
    }

    @Override
    public void fetchEager() {       
        if (getSeries() != null) getSeries().getFamilys().size();
        getModels().forEach(m -> m.getSpecs().size());
    }

}
