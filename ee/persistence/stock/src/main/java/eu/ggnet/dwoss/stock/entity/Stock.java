package eu.ggnet.dwoss.stock.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.*;

import static eu.ggnet.dwoss.rules.SalesChannel.UNKNOWN;

/**
 * Represents a physical stock.
 * <p/>
 * @has n - 1 Stock.ID
 */
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"units", "stockLocations"})
public class Stock implements Serializable {

    @Id
    @Getter
    private int id;

    @Version
    private short optLock;

    @NotNull
    @Size(min = 5)
    @Setter
    @Getter
    private String name;

    @NotNull
    @Setter
    @Getter
    private SalesChannel primaryChannel = UNKNOWN;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "stock")
    List<StockUnit> units = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stock")
    List<StockLocation> stockLocations = new ArrayList<>();

    public Stock() {
    }

    public Stock(int id) {
        this.id = id;
    }

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Adds the unit to the stock if unit is not null, bidirectional handling implemented.
     *
     * @param unit the unit to be added, may not be null
     */
    public void addUnit(StockUnit unit) {
        if ( unit == null ) return;
        unit.setStock(this);
    }

    /**
     * Adds the Unit to this Stock spezifing the stock location, bidirectional handling implemented.
     * Wont do anything if either unit or stock location is null or the stock location is not from this stock
     *
     * @param unit          the unit to be added, may not be null
     * @param stockLocation the stock location may not be null
     */
    public void addUnit(StockUnit unit, StockLocation stockLocation) {
        if ( unit == null || stockLocation == null || !stockLocations.contains(stockLocation) ) return;
        unit.setStockLocation(stockLocation);
    }

    public void removeUnit(StockUnit unit) {
        if ( unit == null ) return;
        unit.setStock(null);
    }

    public List<StockUnit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    /**
     * Adds the StockLocation to the Stock, bidirectional handling implemented
     *
     * @param stockLocation the StockLocation to be added
     */
    public void addStockLocation(StockLocation stockLocation) {
        if ( stockLocation == null ) return;
        stockLocation.setStock(this);
    }

    /**
     * Removes the StockLocation for this stock, bidirectional handling implemented
     *
     * @param stockLocation the StockLocation to be removed
     */
    public void removeStockLocation(StockLocation stockLocation) {
        if ( stockLocation == null ) return;
        stockLocation.setStock(null);
    }

    public List<StockLocation> getStockLocations() {
        return Collections.unmodifiableList(stockLocations);
    }

}
