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
package eu.ggnet.dwoss.redtape.ee.entity;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * A SalesProduct represents a saleable non "SoPo" unit associated with a UniqueUnit.Product.
 *
 * @author bastian.venz
 * @author oliver.guenther
 * @author pascal.perau
 */
@Entity
@NamedQuery(name = "byUniqueUnitProductId", query = "SELECT s FROM SalesProduct AS s WHERE s.uniqueUnitProductId = ?1")
@SuppressWarnings("PersistenceUnitPresent")
public class SalesProduct implements Serializable {

    /**
     * This represents the id and part number.
     * Every part number can only be once in the database.
     */
    @Id
    private String partNo;

    @Version
    private Short optLock = 0;

    /**
     * This String represents the name of a {@link SalesProduct}.
     */
    @Basic
    @Lob
    @Column(length = 65536)
    private String name;

    /**
     * This float represents the price of a {@link SalesProduct}.
     */
    @Min(value = 0, message = "The price must be over 0!")
    private Double price;

    /**
     * This float represents the UniqueUnit.Product.id of a {@link SalesProduct}.
     */
    @Basic
    private long uniqueUnitProductId;

    /**
     * This string represents the description of a {@link SalesProduct}.
     */
    @Basic
    @Lob
    @Column(length = 65536)
    private String description;

    public static final String PROP_DESCRIPTION = "description";

    public static final String PROP_UNIQUEUNITID = "uniqueUnitProductId";

    public static final String PROP_PRICE = "price";

    public static final String PROP_NAME = "name";

    @Transient
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public SalesProduct() {
    }

    public SalesProduct(String partNo, String name, Double price, long uniqueUnitProductId, String description) {
        this.partNo = partNo;
        this.name = name;
        this.price = price;
        this.uniqueUnitProductId = uniqueUnitProductId;
        this.description = description;
    }

    //<editor-fold defaultstate="collapsed" desc="setter/getter with propertyChangeSupport">
    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        propertyChangeSupport.firePropertyChange(PROP_DESCRIPTION, oldDescription, description);
    }

    /**
     * Get the value of price
     *
     * @return the value of price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set the value of price
     *
     * @param price new value of price
     */
    public void setPrice(Double price) {
        Double oldPrice = this.price;
        this.price = price;
        propertyChangeSupport.firePropertyChange(PROP_PRICE, oldPrice, price);
    }

    /**
     * Get the value of Name
     *
     * @return the value of Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of Name
     *
     * @param Name new value of Name
     */
    public void setName(String Name) {
        String oldName = this.name;
        this.name = Name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, Name);
    }

    /**
     * Get the value of uniqueUnitId
     *
     * @return the value of uniqueUnitId
     */
    public long getUniqueUnitProductId() {
        return uniqueUnitProductId;
    }

    /**
     * Set the value of uniqueUnitId
     *
     * @param uniqueUnitProductId new value of uniqueUnitProductId
     */
    public void setUniqueUnitProductId(long uniqueUnitProductId) {
        long oldUniqueUnitId = this.uniqueUnitProductId;
        this.uniqueUnitProductId = uniqueUnitProductId;
        propertyChangeSupport.firePropertyChange(PROP_UNIQUEUNITID, oldUniqueUnitId, uniqueUnitProductId);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "SalesProduct{" + "partNo=" + partNo + ", name=" + name + ", price=" + price + ", uniqueUnitProductId=" + uniqueUnitProductId + ", description=" + description + '}';
    }

    public String toHtml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>").append(partNo).append(" ").append(name).append("</h1><br>");
        builder.append("Price: ").append(price).append("<br>");
        builder.append("Unique Unit Product Id: ").append(uniqueUnitProductId).append("<br>");
        builder.append("Description: ").append(description);

        return builder.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of partNo">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.partNo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final SalesProduct other = (SalesProduct)obj;
        if ( !Objects.equals(this.partNo, other.partNo) ) return false;
        return true;
    }
    //</editor-fold>

    private void readObject(ObjectInputStream ois) throws IOException {
        try {
            ois.defaultReadObject();
            propertyChangeSupport = new PropertyChangeSupport(this);
        } catch (ClassNotFoundException e) {
            throw new IOException("No class found. HELP!!");
        }
    }
}
