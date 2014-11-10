package eu.ggnet.dwoss.receipt.product;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import eu.ggnet.dwoss.receipt.AbstractController;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.ProductSpec;

import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoFilter;
import eu.ggnet.dwoss.util.table.PojoTableModel;

import static eu.ggnet.saft.core.Client.lookup;


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
        this(lookup(SpecAgent.class));
    }

    public SpecListController(SpecAgent specAgent) {
        specs = new ArrayList<>();
        if ( specAgent != null ) specs = specAgent.findAll(ProductSpec.class);
        this.model = new PojoTableModel(specs,
                new PojoColumn<ProductSpec>("Warengruppe", true, 10, String.class, "model.family.series.group.note"),
                new PojoColumn<ProductSpec>("Brand", true, 10, String.class, "model.family.series.brand.name"),
                new PojoColumn<ProductSpec>("Serie", true, 15, String.class, "model.family.series.name"),
                new PojoColumn<ProductSpec>("Familie", true, 15, String.class, "model.family.name"),
                new PojoColumn<ProductSpec>("Modell", true, 25, String.class, "model.name"),
                new PojoColumn<ProductSpec>("PartNo", true, 10, String.class, "partNo"));
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
