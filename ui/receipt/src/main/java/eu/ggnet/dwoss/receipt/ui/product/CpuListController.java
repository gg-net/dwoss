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

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jakarta.inject.Inject;

import eu.ggnet.dwoss.core.widget.dl.RemoteDl;

import jakarta.annotation.PostConstruct;

import eu.ggnet.dwoss.core.widget.swing.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author pascal.perau
 */
@Dependent
public class CpuListController {

    private class CpuFilter implements PojoFilter<Cpu> {

        private String regexModel = "";

        @Override
        public boolean filter(Cpu c) {
            if ( regexModel == null || regexModel.trim().equals("") ) return true;
            try {
                return Pattern.matches(regexModel, c.getModel())
                        || Pattern.matches(regexModel, c.getManufacturer().getNote())
                        || Pattern.matches(regexModel, c.getSeries().getNote());
            } catch (PatternSyntaxException e) {
                return true;
            }
        }
    }

    @Inject
    private RemoteDl remote;

    private List<Cpu> cpus;

    private PojoTableModel<Cpu> model;

    private CpuFilter filter;

    @PostConstruct
    private void initCdi() {
        cpus = new ArrayList<>();
        cpus = remote.lookup(SpecAgent.class).findAll(Cpu.class);
        this.model = new PojoTableModel(cpus,
                new PojoColumn<Cpu>("Hersteller", 50, String.class, cpu -> cpu.getManufacturer().getNote()),
                new PojoColumn<Cpu>("Serie", 75, String.class, cpu -> cpu.getSeries().getNote()),
                new PojoColumn<>("Modell", 75, String.class, Cpu::getModel),
                new PojoColumn<Cpu>("Typ", 50, Set.class, cpu -> cpu.getTypes()),
                new PojoColumn<>("Kerne", 50, Integer.class, Cpu::getCores),
                new PojoColumn<>("Frequenz", 50, Double.class, Cpu::getFrequency),
                new PojoColumn<>("Name", 100, String.class, Cpu::getName),
                new PojoColumn<>("EV", 20, Double.class, Cpu::getEconomicValue));
        filter = new CpuFilter();
        model.setFilter(filter);
    }

    public PojoTableModel<Cpu> getModel() {
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
    public void editSelected(java.awt.Window parent) {
        EditCpuPanel cpuPanel = new EditCpuPanel(cpus);
        cpuPanel.setCpu(model.getSelected());
        OkCancelDialog<EditCpuPanel> dialog = new OkCancelDialog<>(parent, "CPU bearbeiten", cpuPanel);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            model.remove(model.getSelected());
            Cpu editCpu = cpuPanel.getCpu();
            model.add(editCpu);
        }
    }
}
