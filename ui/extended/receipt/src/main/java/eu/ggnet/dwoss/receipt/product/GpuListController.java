package eu.ggnet.dwoss.receipt.product;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.SwingUtilities;

import eu.ggnet.dwoss.receipt.AbstractController;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

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
        this(lookup(SpecAgent.class));
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
