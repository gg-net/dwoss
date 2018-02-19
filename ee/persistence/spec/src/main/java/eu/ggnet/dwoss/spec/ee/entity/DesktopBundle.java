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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import static javax.persistence.CascadeType.*;

/**
 * Represents a combination of individual {@link Desktop} and {@link Monitor} packed.
 * @author bastian.venz
 */
// TODO: Ensure that the Specs are only the Considered Details -- Still in consideration? - PP
// Persistence Hint: Not in Spec
@Entity
public class DesktopBundle extends ProductSpec {
        
    @XmlElement(type = Desktop.class)
    @NotNull
    @ManyToOne(cascade={DETACH,MERGE,REFRESH,PERSIST},optional=false)
    private ProductSpec desktop;

    @XmlElement(type = Monitor.class)
    @NotNull
    @ManyToOne(cascade={DETACH,MERGE,REFRESH,PERSIST},optional=false)
    private ProductSpec monitor;

    public DesktopBundle() {
    }

    public DesktopBundle(Desktop desktop, Monitor monitor) {
        this.desktop = desktop;
        this.monitor = monitor;
    }

    public ProductSpec getDesktop() {
        return desktop;
    }

    public void setDesktop(ProductSpec desktop) {
        this.desktop = desktop;
    }

    public ProductSpec getMonitor() {
        return monitor;
    }

    public void setMonitor(ProductSpec monitor) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        return "DesktopBundle{"+ super.toString() + ",desktop=" + desktop + ", monitor=" + monitor + '}';
    }

}
