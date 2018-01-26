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
package eu.ggnet.dwoss.redtape.ee.sage.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"attrname", "fieldtype","width"})
public class Field {

    @XmlAttribute(name = "width")
    private Integer width;

    @XmlAttribute(name = "fieldtype")
    private String fieldtype;

    @XmlAttribute(name = "attrname")
    private String attrname;

    public Field() {
    }

    public Field(String attrname, String fieldtype) {
        this.attrname = attrname;
        this.fieldtype = fieldtype;
    }

    public Field(String attrname, String fieldtype, Integer width) {
        this.attrname = attrname;
        this.fieldtype = fieldtype;
        this.width = width;
    }

}
