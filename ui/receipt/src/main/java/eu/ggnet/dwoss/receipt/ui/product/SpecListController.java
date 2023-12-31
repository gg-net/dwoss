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
package eu.ggnet.dwoss.receipt.ui.product;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.inject.Inject;

import eu.ggnet.dwoss.core.common.values.ProductGroup;

import jakarta.annotation.PostConstruct;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.swing.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.*;

import jakarta.enterprise.context.Dependent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pascal.perau
 */
@Dependent
public class SpecListController {

    private class SpecFilter implements PojoFilter<ProductSpec> {

        private String regexModel = "";

        @Override
        public boolean filter(ProductSpec s) {
            if ( regexModel == null || regexModel.trim().equals("") ) return true;
            try {
                return Pattern.matches(regexModel, s.getModel().getFamily().getSeries().getGroup().getNote())
                        || Pattern.matches(regexModel, s.getModel().getFamily().getSeries().getName())
                        || Pattern.matches(regexModel, s.getModel().getFamily().getName())
                        || Pattern.matches(regexModel, s.getModel().getName());
            } catch (PatternSyntaxException | NullPointerException e) {
                return true;
            }
        }
    }

    private SpecFilter filter;

    private List<ProductSpec> specs;

    private PojoTableModel<ProductSpec> model;

    @Inject
    private RemoteDl remote;

    @PostConstruct
    private void initCdi() {
        specs = remote.lookup(SpecAgent.class).findAll(ProductSpec.class);
        this.model = new PojoTableModel<>(specs,
                new PojoColumn<>("Warengruppe", 10, String.class, spec -> Optional.ofNullable(spec).map(ProductSpec::getModel).map(ProductModel::getFamily).map(ProductFamily::getSeries).map(ProductSeries::getGroup).map(ProductGroup::getNote).orElse("")),
                new PojoColumn<>("Brand", 10, String.class, spec -> Optional.ofNullable(spec).map(ProductSpec::getModel).map(ProductModel::getFamily).map(ProductFamily::getSeries).map(ProductSeries::getBrand).map(TradeName::getDescription).orElse("")),
                new PojoColumn<>("Serie", 15, String.class, spec -> Optional.ofNullable(spec).map(ProductSpec::getModel).map(ProductModel::getFamily).map(ProductFamily::getSeries).map(ProductSeries::getName).orElse("")),
                new PojoColumn<>("Familie", 15, String.class, spec -> Optional.ofNullable(spec).map(ProductSpec::getModel).map(ProductModel::getFamily).map(ProductFamily::getName).orElse("")),
                new PojoColumn<>("Modell", 25, String.class, spec -> Optional.ofNullable(spec).map(ProductSpec::getModel).map(ProductModel::getName).orElse("")),
                new PojoColumn<>("PartNo", 10, String.class, ProductSpec::getPartNo));
        filter = new SpecFilter();
        model.setFilter(filter);
    }

    public List<ProductSpec> getSpecs() {
        return specs;
    }

    public PojoTableModel<ProductSpec> getModel() {
        return model;
    }

    /**
     * Adds a usable Filter to the model.
     * <p/>
     * @param search the search string.
     */
    public void filter(String search) {
        if ( search != null ) search = ".*" + search.replaceAll("\\*", "") + ".*";
        filter.regexModel = "(?i)" + search;
        model.fireTableDataChanged();
    }
}
