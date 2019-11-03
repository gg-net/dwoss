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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import eu.ggnet.dwoss.common.ui.table.*;
import eu.ggnet.dwoss.receipt.ui.AbstractController;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.saft.core.Dl;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pascal.perau
 */
public class SpecListController extends AbstractController {

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

    public SpecListController() {
        this(Dl.remote().lookup(SpecAgent.class));
    }

    public SpecListController(SpecAgent specAgent) {
        specs = new ArrayList<>();
        if ( specAgent != null ) specs = specAgent.findAll(ProductSpec.class);
        this.model = new PojoTableModel(specs,
                new PojoColumn<>("Warengruppe", true, 10, String.class, "model.family.series.group.note"),
                new PojoColumn<>("Brand", true, 10, String.class, "model.family.series.brand.name"),
                new PojoColumn<>("Serie", true, 15, String.class, "model.family.series.name"),
                new PojoColumn<>("Familie", true, 15, String.class, "model.family.name"),
                new PojoColumn<>("Modell", true, 25, String.class, "model.name"),
                new PojoColumn<>("PartNo", true, 10, String.class, "partNo"));
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
