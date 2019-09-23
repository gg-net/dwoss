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

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;

/**
 *
 * @author pascal.perau
 */
@FreeBuilder
public abstract class ListingConfiguration implements Serializable {

    public abstract Optional<String> jasperTemplateFile();

    public abstract Optional<String> jasperTempleteUnitsFile();

    public abstract Optional<URL> logoLeft();

    public abstract Optional<URL> logoRight();

    public abstract Optional<String> orderLink();

    public abstract String filePrefix();

    public abstract String name();

    public abstract TradeName brand();

    public abstract Set<TradeName> supplementBrands();

    public abstract Set<ProductGroup> groups();

    public abstract String headLeft();

    public abstract String headCenter();

    public abstract String headRight();

    public abstract String footer();

    public static class Builder extends ListingConfiguration_Builder {
    };

    public ListingConfiguration copyWith(String name, Set<ProductGroup> groups) {
        return new ListingConfiguration.Builder().mergeFrom(this).name(name).addAllGroups(groups).build();
    }

    public ListingConfiguration copyWith(TradeName brand, URL logoLeft, String orderLink) {
        return new ListingConfiguration.Builder().mergeFrom(this).brand(brand).addAllSupplementBrands(EnumSet.noneOf(TradeName.class))
                .logoLeft(logoLeft).orderLink(orderLink).build();
    }

    public Set<TradeName> getAllBrands() {
        EnumSet<TradeName> result = EnumSet.of(brand());
        result.addAll(supplementBrands());
        return result;
    }

    public Map<String, Object> toReportParamters() {
        Map<String, Object> reportParameter = new HashMap<>();
        reportParameter.put("SUB_REPORT", jasperTempleteUnitsFile().orElseThrow(
                () -> new NullPointerException("JasperTemplateUnitsFile not set in toReportParameters. Unsing " + this)));
        logoLeft().ifPresent(l -> reportParameter.put("BRAND_LOGO", l));
        logoRight().ifPresent(l -> reportParameter.put("COMPANY_LOGO", l));
        orderLink().ifPresent(o -> reportParameter.put("ORDERLINK", o));
        reportParameter.put("TITLE", name());
        reportParameter.put("REPORT_LOCALE", Locale.GERMANY);
        reportParameter.put("HEAD_LEFT", headLeft());
        reportParameter.put("HEAD_CENTER", headCenter());
        reportParameter.put("HEAD_RIGHT", headRight());
        reportParameter.put("FOOTER", footer());
        return reportParameter;
    }

}
