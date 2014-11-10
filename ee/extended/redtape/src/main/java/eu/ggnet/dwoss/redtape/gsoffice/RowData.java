package eu.ggnet.dwoss.redtape.gsoffice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DATAPACKET")
public class RowData {

    @XmlElement(name = "METADATA")
    List<Fields> fieldss;
    @XmlElementWrapper(name = "ROWDATA")
    @XmlElement(name = "ROW")
    List<Row> listOfRows;
    @XmlAttribute(name = "Version")
    String version = "2.0";

    public RowData() {
        fieldss = new ArrayList<>();
        Fields fields = new Fields();
        fieldss.add(fields);

        fields.add(new Field("B_EBBUCHUNG", "i4"));
        fields.add(new Field("B_EGKONTO", "i4"));
        fields.add(new Field("BELEG", "string", 20));
        fields.add(new Field("BEREICH", "string", 1));
        fields.add(new Field("BETRAG", "r8"));
        fields.add(new Field("BETRAGN", "r8"));
        fields.add(new Field("BETRAGS", "r8"));
        fields.add(new Field("BUCHTEXT", "string", 80));
        fields.add(new Field("BUERFDATUM", "datetime"));
        fields.add(new Field("DATUM", "datetime"));
        fields.add(new Field("EXTERNEOPNUMMER", "string", 20));
        fields.add(new Field("FADATUM", "datetime"));
        fields.add(new Field("GKONTO", "string", 12));
        fields.add(new Field("INTERNEOPID", "i4"));
        fields.add(new Field("KAKENN", "string", 32));
        fields.add(new Field("KBKENN", "string", 32));
        fields.add(new Field("KONTO", "string", 12));
        fields.add(new Field("PROGRAMM", "string", 10));
        fields.add(new Field("REDATUM", "datetime"));
        fields.add(new Field("SKBETRAG", "r8"));
        fields.add(new Field("SKCODE", "string", 2));
        fields.add(new Field("SKPROZ", "r8"));
        fields.add(new Field("START", "string", 1));
        fields.add(new Field("STCODE", "string", 2));
        fields.add(new Field("STPROZ", "r8"));
        fields.add(new Field("USTIDNR", "string", 20));
        fields.add(new Field("WAWIBELEG", "string", 20));

        listOfRows = new ArrayList<>();
    }

    public boolean add(Row e) {
        return listOfRows.add(e);
    }

}
