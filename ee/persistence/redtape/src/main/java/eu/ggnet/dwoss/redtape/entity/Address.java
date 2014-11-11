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
package eu.ggnet.dwoss.redtape.entity;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.persistence.*;

import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

/**
 *
 * @author bastian.venz
 */
@Entity
// TODO: It seams that HSQL DB does not have this .... put it at least in the mysql create
// @Table(uniqueConstraints=@UniqueConstraint(columnNames="description",name="unique_address_description"))
@NamedQuery(name = "byFormatedString", query = "select a from Address as a where a.description = ?1")
public class Address extends IdentifiableEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Basic(optional = false)
    @Column(length = 65536)
    @Lob
    private String description;

    public static final String PROP_DESCRIPTION = "description";

    @Transient
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public Address() {
    }

    public Address(String description) {
        this.description = description;
    }

    @Override
    public long getId() {
        return id;
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

    @Override
    public String toString() {
        String des = description;
        if ( des != null ) des = des.replaceAll("\\n", "\\\\n");
        return "Address{" + "id=" + id + ", description=" + des + '}';
    }
}
