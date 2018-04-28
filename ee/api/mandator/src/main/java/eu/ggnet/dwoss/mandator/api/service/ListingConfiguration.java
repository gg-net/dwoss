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
package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;
import java.net.URL;
import java.util.*;

import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;

import lombok.*;

import static java.util.EnumSet.noneOf;

/**
 *
 * @author pascal.perau
 */
@Data
@AllArgsConstructor
public class ListingConfiguration implements Serializable {

    @NotNull
    private String jasperTemplateFile;

    @NotNull
    private String jasperTempleteUnitsFile;

    private URL logoLeft;

    private URL logoRight;

    private String orderLink;

    private String filePrefix;

    private String name;

    private TradeName brand;

    private Set<TradeName> supplementBrands;

    private Set<ProductGroup> groups;

    private String headLeft;

    private String headCenter;

    private String headRight;

    private String footer;

    @Builder
    public ListingConfiguration(String name,
                                URL logoLeft, URL logoRight,
                                String orderLink, String filePrefix,
                                TradeName brand,
                                Set<TradeName> supplementBrands,
                                Set<ProductGroup> groups,
                                String headLeft,
                                String headCenter,
                                String headRight,
                                String footer) {
        this.name = name;
        this.logoLeft = logoLeft;
        this.logoRight = logoRight;
        this.orderLink = orderLink;
        this.filePrefix = filePrefix;
        this.brand = brand;
        this.supplementBrands = supplementBrands;
        this.groups = groups;
        this.headLeft = headLeft;
        this.headRight = headRight;
        this.headCenter = headCenter;
        this.footer = footer;
    }

    public ListingConfiguration copyWith(String name, Set<ProductGroup> groups) {
        return new ListingConfiguration(jasperTemplateFile, jasperTempleteUnitsFile, logoLeft, logoRight, orderLink, filePrefix, name, brand,
                supplementBrands, groups, headLeft, headCenter, headRight, footer);
    }

    public ListingConfiguration copyWith(TradeName brand, URL logoLeft, String orderLink) {
        return new ListingConfiguration(jasperTemplateFile, jasperTempleteUnitsFile, logoLeft, logoRight, orderLink, filePrefix, name, brand,
                noneOf(TradeName.class), groups, headLeft, headCenter, headRight, footer);
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
        reportParameter.put("HEAD_LEFT", headLeft);
        reportParameter.put("HEAD_CENTER", headCenter);
        reportParameter.put("HEAD_RIGHT", headRight);
        reportParameter.put("FOOTER", footer);
        return reportParameter;
    }

}
