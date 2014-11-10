package eu.ggnet.dwoss.redtape.gsoffice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Fields {

    @XmlElementWrapper(name = "FIELDS")
    @XmlElement(name = "FIELD")
    List<Field> fields;

    public Fields() {
        fields = new ArrayList<Field>();
    }

    public boolean add(Field e) {
        return fields.add(e);
    }

}
