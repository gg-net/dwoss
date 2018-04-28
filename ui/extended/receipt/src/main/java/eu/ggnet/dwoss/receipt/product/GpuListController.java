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
package eu.ggnet.dwoss.receipt.product;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.common.ui.CloseType;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.common.ui.table.*;
import eu.ggnet.dwoss.receipt.AbstractController;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.saft.Dl;

/**
 *
 * @author pascal.perau
 */
public class GpuListController extends AbstractController {

    private class GpuFilter implements PojoFilter<Gpu> {

        private String regexModel = "";

        @Override
        public boolean filter(Gpu g) {
            if ( regexModel == null || regexModel.trim().equals("") ) return true;
            try {
                return Pattern.matches(regexModel, g.getModel())
                        || Pattern.matches(regexModel, g.getManufacturer().getNote())
                        || Pattern.matches(regexModel, g.getSeries().getNote());
            } catch (PatternSyntaxException e) {
                return true;
            }
        }
    }

    private List<Gpu> allGpus;

    private PojoTableModel<Gpu> model;

    GpuFilter filter;

    public GpuListController() {
        this(Dl.remote().lookup(SpecAgent.class));
    }

    public GpuListController(SpecAgent specAgent) {
        allGpus = new ArrayList<>();
        if ( specAgent != null ) allGpus = specAgent.findAll(Gpu.class);
        this.model = new PojoTableModel(allGpus,
                new PojoColumn<Gpu>("Hersteller", true, 50, String.class, "manufacturer.note"),
                new PojoColumn<Gpu>("Serie", true, 75, String.class, "series.note"),
                new PojoColumn<Gpu>("Typ", true, 50, EnumSet.class, "types"),
                new PojoColumn<Gpu>("Modell", true, 75, String.class, "model"),
                new PojoColumn<Gpu>("Name", true, 100, String.class, "name"),
                new PojoColumn<Gpu>("EV", true, 20, Double.class, "economicValue"));
        filter = new GpuFilter();
        model.setFilter(filter);
    }

    public PojoTableModel<Gpu> getModel() {
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

    /**
     * Open EditCpuPanel via table selection
     */
    public void editSelected() {
        EditGpuPanel gpuPanel = new EditGpuPanel(allGpus);
        gpuPanel.setGpu(model.getSelected());
        OkCancelDialog<EditGpuPanel> dialog = new OkCancelDialog<>(SwingUtilities.getWindowAncestor(this.view), "GPU bearbeiten", gpuPanel);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            model.remove(model.getSelected());
            Gpu editGpu = gpuPanel.getGpu();
            model.add(editGpu);
        }
    }

}
