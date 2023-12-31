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

import java.awt.Window;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.inject.Inject;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;

import jakarta.annotation.PostConstruct;

import eu.ggnet.dwoss.core.widget.swing.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class GpuListController {

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

    private GpuFilter filter;

    @Inject
    private RemoteDl remote;

    @PostConstruct
    private void initCdi() {
        allGpus = remote.lookup(SpecAgent.class).findAll(Gpu.class);
        this.model = new PojoTableModel<>(allGpus,
                new PojoColumn<>("Hersteller", 50, String.class, gpu -> gpu.getManufacturer().getNote()),
                new PojoColumn<>("Serie", 75, String.class, gpu -> gpu.getSeries().getNote()),
                new PojoColumn<>("Typ", 50, Set.class, Gpu::getTypes),
                new PojoColumn<>("Modell", 75, String.class, Gpu::getModel),
                new PojoColumn<>("Name", 100, String.class, Gpu::getName),
                new PojoColumn<>("EV", 20, Double.class, Gpu::getEconomicValue));
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
    public void editSelected(Window window) {
        EditGpuPanel gpuPanel = new EditGpuPanel(allGpus);
        gpuPanel.setGpu(model.getSelected());
        OkCancelDialog<EditGpuPanel> dialog = new OkCancelDialog<>(window, "GPU bearbeiten", gpuPanel);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            model.remove(model.getSelected());
            Gpu editGpu = gpuPanel.getGpu();
            model.add(editGpu);
        }
    }

}
