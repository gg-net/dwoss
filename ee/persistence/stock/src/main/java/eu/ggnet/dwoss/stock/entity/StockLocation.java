package eu.ggnet.dwoss.stock.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents a location in a stock.
 * e.g. Shelf with number 5, sale item shelf, repair shelf
 * <p/>
 * @has n - 1 Stock
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "StockLocation.byStockLikeName", query = "SELECT sl FROM StockLocation sl WHERE sl.stock = ?1 AND sl.name LIKE ?2"),
    @NamedQuery(name = "StockLocation.likeName", query = "SELECT sl FROM StockLocation sl WHERE sl.name LIKE ?1")
})
public class StockLocation implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private short optLock;

    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    private Stock stock;

    @NotNull
    @Basic(optional = false)
    private String name;

    private String description;

    public int getId() {
        return id;
    }

    public StockLocation() {
    }

    public StockLocation(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        if ( stock == null && this.stock == null ) return;
        if ( this.stock != null && this.stock.equals(stock) ) return;
        if ( this.stock != null ) {
            this.stock.stockLocations.remove(this);
        }
        if ( stock != null ) {
            stock.stockLocations.add(this);
        }
        this.stock = stock;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StockLocation other = (StockLocation)obj;
        if ( this.id != other.id ) return false;
        if ( this.stock != other.stock && (this.stock == null || !this.stock.equals(other.stock)) ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
        hash = 23 * hash + (this.stock != null ? this.stock.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "StockLocation{" + "id=" + id + ",stock=" + (stock == null ? null : stock.getName()) + ",name=" + name + ",description=" + description + '}';
    }
}
