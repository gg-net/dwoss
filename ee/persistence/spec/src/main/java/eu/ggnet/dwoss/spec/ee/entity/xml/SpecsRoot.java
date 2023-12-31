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
package eu.ggnet.dwoss.spec.ee.entity.xml;

import java.io.Serializable;
import java.util.*;

import jakarta.xml.bind.annotation.*;

import eu.ggnet.dwoss.spec.ee.entity.*;

/**
 *
 * @author oliver.guenther
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpecsRoot implements Serializable {

    @XmlElements({
        @XmlElement(name = "notebook", type = Notebook.class),
        @XmlElement(name = "basic", type = BasicSpec.class),
        @XmlElement(name = "desktop", type = Desktop.class),
        @XmlElement(name = "allInOne", type = AllInOne.class),
        @XmlElement(name = "monitor", type = Monitor.class),
        @XmlElement(name = "tablet", type = Tablet.class)
    })
    private List<? extends ProductSpec> productSpecs;

    public SpecsRoot() {
        productSpecs = new ArrayList<>();
    }

    public SpecsRoot(List<? extends ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public List<? extends ProductSpec> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List<? extends ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
    }

}
