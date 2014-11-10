package eu.ggnet.dwoss.redtape.gsoffice;

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
