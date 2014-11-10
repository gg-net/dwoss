package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.util.EnumSet.noneOf;

/**
 *
 * @author pascal.perau
 */
@Data
@AllArgsConstructor
public class ListingConfiguration implements Serializable {

    private String jasperTemplateFile;

    private String jasperTempleteUnitsFile;

    private URL logoLeft;

    private URL logoRight;

    private String orderLink;

    private String filePrefix;

    private String name;

    private TradeName brand;

    private Set<TradeName> supplementBrands;

    private Set<ProductGroup> groups;

    public ListingConfiguration(String jasperTemplateFile,
                                String jasperTempleteUnitsFile,
                                URL logoLeft, URL logoRight,
                                String orderLink, String filePrefix,
                                TradeName brand, Set<TradeName> supplementBrands) {
        this.jasperTemplateFile = jasperTemplateFile;
        this.jasperTempleteUnitsFile = jasperTempleteUnitsFile;
        this.logoLeft = logoLeft;
        this.logoRight = logoRight;
        this.orderLink = orderLink;
        this.filePrefix = filePrefix;
        this.brand = brand;
        this.supplementBrands = supplementBrands;
    }

    public ListingConfiguration copyWith(String name, Set<ProductGroup> groups) {
        return new ListingConfiguration(jasperTemplateFile, jasperTempleteUnitsFile, logoLeft, logoRight, orderLink, filePrefix, name, brand, supplementBrands, groups);
    }

    public ListingConfiguration copyWith(TradeName brand, URL logoLeft, String orderLink) {
        return new ListingConfiguration(jasperTemplateFile, jasperTempleteUnitsFile, logoLeft, logoRight, orderLink, filePrefix, name, brand, noneOf(TradeName.class), groups);
    }

    public Set<TradeName> getAllBrands() {
        EnumSet<TradeName> result = EnumSet.of(brand);
        if ( supplementBrands != null && !supplementBrands.isEmpty() ) result.addAll(supplementBrands);
        return result;
    }

    public Map<String, Object> toReportParamters() {
        Map<String, Object> reportParameter = new HashMap<>();
        reportParameter.put("SUB_REPORT", jasperTempleteUnitsFile);
        reportParameter.put("BRAND_LOGO", logoLeft);
        reportParameter.put("COMPANY_LOGO", logoRight);
        reportParameter.put("TITLE", name);
        reportParameter.put("ORDERLINK", orderLink);
        reportParameter.put("REPORT_LOCALE", Locale.GERMANY);
        return reportParameter;
    }

}
