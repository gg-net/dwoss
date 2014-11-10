package eu.ggnet.dwoss.spec.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static javax.persistence.CascadeType.*;

/**
 *
 * @composed 1 - n ProductFamily
 * @author oliver.guenther
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ProductModel.byName", query = "select m from ProductModel m where m.name = ?1"),
    @NamedQuery(name = "ProductModel.byNameFamilySeries",
                query = "select m from ProductModel m where m.name = ?5 and m.family.name = ?4 "
                + "and m.family.series.brand = ?1 and m.family.series.group = ?2 and m.family.series.name = ?3")
})
@EqualsAndHashCode(of = "id")
public class ProductModel implements Serializable, INamed, EagerAble {

    @XmlTransient
    @Id
    @GeneratedValue
    @Getter
    private long id;

    @XmlTransient
    @Version
    private short optLock;

    @XmlAttribute
    @Basic(optional = false)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    @Getter
    private ProductFamily family;

    @NotNull
    @OneToMany(cascade = {DETACH, REFRESH}, mappedBy = "model")
    @XmlTransient
    Set<ProductSpec> specs = new HashSet<>();

    @Column(columnDefinition = "DECIMAL(7,2)")
    @XmlAttribute
    @Getter
    @Setter
    private Double economicValue;

    public ProductModel() {
    }

    ProductModel(long id) {
        this.id = id;
    }

    public ProductModel(String name) {
        this.name = name;
    }

    public ProductModel(String name, ProductFamily family) {
        this.name = name;
        setFamily(family);
    }

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
        if ( getFamily() != null ) getFamily().getSeries();
        getSpecs().size();
    }

    @Override
    public String toString() {
        return "ProductModel{" + "id=" + id + ", name=" + name + ", family=" + family + ", economicValue=" + economicValue + '}';
    }
}
