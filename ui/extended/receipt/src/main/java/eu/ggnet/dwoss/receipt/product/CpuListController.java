/* 
 * Copyright (C) 2014 pascal.perau
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.receipt.AbstractController;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;

import eu.ggnet.dwoss.util.CloseType;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.util.table.PojoColumn;
import eu.ggnet.dwoss.util.table.PojoFilter;
import eu.ggnet.dwoss.util.table.PojoTableModel;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class CpuListController extends AbstractController {

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

    private List<Cpu> cpus;

    private PojoTableModel<Cpu> model;

    private CpuFilter filter;

    public CpuListController() {
        this(lookup(SpecAgent.class));
    }

    public CpuListController(SpecAgent specAgent) {
        cpus = new ArrayList<>();
        if ( specAgent != null ) cpus = specAgent.findAll(Cpu.class);
        this.model = new PojoTableModel(cpus,
                new PojoColumn<Cpu>("Hersteller", true, 50, String.class, "manufacturer.note"),
                new PojoColumn<Cpu>("Serie", true, 75, String.class, "series.note"),
                new PojoColumn<Cpu>("Modell", true, 75, String.class, "model"),
                new PojoColumn<Cpu>("Typ", true, 50, EnumSet.class, "types"),
                new PojoColumn<Cpu>("Kerne", true, 50, Integer.class, "cores"),
                new PojoColumn<Cpu>("Frequenz", true, 50, Double.class, "frequency"),
                new PojoColumn<Cpu>("Name", true, 100, String.class, "name"),
                new PojoColumn<Cpu>("EV", true, 20, Double.class, "economicValue"));
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
    public void editSelected() {
        EditCpuPanel cpuPanel = new EditCpuPanel(cpus);
        cpuPanel.setCpu(model.getSelected());
        OkCancelDialog<EditCpuPanel> dialog = new OkCancelDialog<>(SwingUtilities.getWindowAncestor(this.view), "CPU bearbeiten", cpuPanel);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            model.remove(model.getSelected());
            Cpu editCpu = cpuPanel.getCpu();
            model.add(editCpu);
        }
    }
}
