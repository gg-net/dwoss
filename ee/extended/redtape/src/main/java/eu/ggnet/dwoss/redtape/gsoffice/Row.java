package eu.ggnet.dwoss.redtape.gsoffice;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"wawiBeleg", "stCode", "reDatum", "konto", "GKonto",
    "faDatum", "datum", "buerfDatum", "buchtext", "beleg",
    "stProz", "betragS", "betragN", "b_egkonto", "b_ebbuchung", "betrag"})
public class Row {

    /**
     * Belegnummer
     */
    @XmlAttribute(name = "BELEG")
    private String beleg;

    /**
     * Bruttobetrag
     */
    @XmlAttribute(name = "BETRAG")
    private String betrag;

    /**
     * Buchungstext
     */
    @XmlAttribute(name = "BUCHTEXT")
    private String buchtext;

    @XmlAttribute(name = "B_EBBUCHUNG")
    private int b_ebbuchung;

    @XmlAttribute(name = "B_EGKONTO")
    private int b_egkonto;

    @XmlAttribute(name = "BEREICH")
    private String bereich;

    /**
     * Nettobetrag
     */
    @XmlAttribute(name = "BETRAGN")
    private String betragN;

    /**
     * Steuer anteilig
     */
    @XmlAttribute(name = "BETRAGS")
    private String betragS;

    @XmlAttribute(name = "BUERFDATUM")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date buerfDatum;

    @XmlAttribute(name = "DATUM")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date datum;

    @XmlAttribute(name = "EXTERNEOPNUMMER")
    private String externeOpNummer;

    @XmlAttribute(name = "FADATUM")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date faDatum;

    /**
     * Gegenkonto
     */
    @XmlAttribute(name = "GKONTO")
    private Integer GKonto;

    @XmlAttribute(name = "INTERNEOPID")
    private Integer interneOpId;

    @XmlAttribute(name = "KAKENN")
    private String kaKenn;

    @XmlAttribute(name = "KBKENN")
    private String kbKenn;

    /**
     * Sollkonto
     */
    @XmlAttribute(name = "KONTO")
    private Integer konto;

    @XmlAttribute(name = "PROGRAMM")
    private String programm;

    @XmlAttribute(name = "REDATUM")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date reDatum;

    @XmlAttribute(name = "SKBETRAG")
    private String skBetrag;

    @XmlAttribute(name = "SKCODE")
    private String skCode;

    @XmlAttribute(name = "SKPROZ")
    private String skProz;

    @XmlAttribute(name = "START")
    private String start;

    /**
     * Steuerschlüssel
     */
    @XmlAttribute(name = "STCODE")
    private String stCode;

    /**
     * Steuer in Prozent
     */
    @XmlAttribute(name = "STPROZ")
    private String stProz;
    /**
     * Steuer in numerischer form
     */
    @XmlTransient
    private double stNumeric;

    @XmlAttribute(name = "USTIDNR")
    private String ustIdNr;

    /**
     * Warenwirtschafts Beleg
     */
    @XmlAttribute(name = "WAWIBELEG")
    protected String wawiBeleg;

    @XmlTransient
    private double nettoSumme;

    @XmlTransient
    private double bruttoSumme;

    public static final NumberFormat NF = new DecimalFormat("0.00",DecimalFormatSymbols.getInstance(Locale.GERMANY));

    public static final NumberFormat NF2 = new DecimalFormat("0");

    public Row() {
    }

    public Row(Row row) {
        this(row.beleg, row.betrag, row.buchtext, row.b_ebbuchung, row.b_egkonto, row.bereich, row.betragN, row.betragS, row.buerfDatum, row.datum, row.externeOpNummer, row.faDatum, row.GKonto, row.interneOpId, row.kaKenn, row.kbKenn, row.konto, row.programm, row.reDatum, row.skBetrag, row.skCode, row.skProz, row.start, row.stCode, row.stProz, row.ustIdNr, row.wawiBeleg);
    }

    public Row(String beleg, String betrag, String buchtext, int b_ebbuchung, int b_egkonto, String bereich, String betragN, String betragS, Date buerfDatum, Date datum, String externeOpNummer, Date faDatum, Integer GKonto, Integer interneOpId, String kaKenn, String kbKenn, Integer konto, String programm, Date reDatum, String skBetrag, String skCode, String skProz, String start, String stCode, String stProz, String ustIdNr, String wawiBeleg) {
        this.beleg = beleg;
        this.betrag = betrag;
        this.buchtext = buchtext;
        this.b_ebbuchung = b_ebbuchung;
        this.b_egkonto = b_egkonto;
        this.bereich = bereich;
        this.betragN = betragN;
        this.betragS = betragS;
        this.buerfDatum = buerfDatum;
        this.datum = datum;
        this.externeOpNummer = externeOpNummer;
        this.faDatum = faDatum;
        this.GKonto = GKonto;
        this.interneOpId = interneOpId;
        this.kaKenn = kaKenn;
        this.kbKenn = kbKenn;
        this.konto = konto;
        this.programm = programm;
        this.reDatum = reDatum;
        this.skBetrag = skBetrag;
        this.skCode = skCode;
        this.skProz = skProz;
        this.start = start;
        this.stCode = stCode;
        this.stProz = stProz;
        this.ustIdNr = ustIdNr;
        this.wawiBeleg = wawiBeleg;

    }

    public String getBeleg() {
        return beleg;
    }

    public void setBeleg(String beleg) {
        this.beleg = beleg;
    }

    public Date getBuerfDatum() {
        return buerfDatum;
    }

    public void setBuerfDatum(Date buerfDatum) {
        this.buerfDatum = buerfDatum;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Date getFaDatum() {
        return faDatum;
    }

    public void setFaDatum(Date faDatum) {
        this.faDatum = faDatum;
    }

    public Date getReDatum() {
        return reDatum;
    }

    public void setReDatum(Date reDatum) {
        this.reDatum = reDatum;
    }

    public String getBuchtext() {
        return buchtext;
    }

    public void setBuchtext(String buchtext) {
        this.buchtext = buchtext;
    }

    public String getStCode() {
        return stCode;
    }

    public void setStCode(String stCode) {
        this.stCode = stCode;
    }

    public Integer getGKonto() {
        return GKonto;
    }

    public void setGKonto(Integer GKonto) {
        this.GKonto = GKonto;
    }

    public Integer getKonto() {
        return konto;
    }

    public void setKonto(Integer konto) {
        this.konto = konto;
    }

    public void setBetrag(double netto, double brutto) {
        this.betragN = NF.format(netto);
        this.betragS = NF.format(brutto - netto);
        this.betrag = NF.format(brutto);
    }

    public String getWawiBeleg() {
        return wawiBeleg;
    }

    public void setWawiBeleg(String value) {
        this.wawiBeleg = value;
    }

    public String getStProz() {
        return stProz;
    }

    public void setStProz(Double stProz) {
        this.stProz = NF2.format(stProz);
    }

    public void setStNumeric(double stNumeric) {
        this.stNumeric = stNumeric;
    }

    public double getStNumeric() {
        return stNumeric;
    }

    public double getNettoSumme() {
        return nettoSumme;
    }

    public void setNettoSumme(double nettoSumme) {
        this.nettoSumme = nettoSumme;
    }

    public double getBruttoSumme() {
        return bruttoSumme;
    }

    public void setBruttoSumme(double bruttoSumme) {
        this.bruttoSumme = bruttoSumme;
    }
}
