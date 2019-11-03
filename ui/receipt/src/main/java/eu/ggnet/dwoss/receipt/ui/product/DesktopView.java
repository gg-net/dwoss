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

import eu.ggnet.dwoss.core.widget.swing.ComboBoxController;
import eu.ggnet.dwoss.core.widget.swing.OkCancelDialog;
import eu.ggnet.dwoss.core.widget.swing.CloseType;
import eu.ggnet.dwoss.core.widget.swing.IPreClose;
import eu.ggnet.dwoss.core.widget.swing.NamedEnumCellRenderer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.Desktop;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Hdd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Odd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Os;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.saft.core.Dl;

/**
 *
 * @author pascal.perau
 */
public class DesktopView extends AbstractView<Desktop> implements IPreClose {

    private static class HddController {

        private ComboBoxController<Hdd.Type> types;

        private ComboBoxController<Hdd> hdds;

        private JCheckBox selection;

        public HddController(final JComboBox typeBox, final JComboBox hddBox, final JCheckBox selection) {
            hddBox.setRenderer(new HddSizeCellRenderer());
            types = new ComboBoxController<>(typeBox, Hdd.Type.values());
            hdds = new ComboBoxController<>(hddBox, Hdd.values());
            this.selection = selection;
            this.selection.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setEnable(selection.isSelected());
                }
            ;
            });
            typeBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hdds.replaceElements(Hdd.getByType(types.getSelected()));
                }
            });
            setHdd(Hdd.ROTATING_0120);
            setEnable(false);
        }

        public final void setHdd(Hdd hdd) {
            if ( hdd == null ) return;
            types.setSelected(hdd.getType());
            hdds.setSelected(hdd);
            setEnable(true);
        }

        public final void setEnable(boolean enable) {
            types.setEnabled(enable);
            hdds.setEnabled(enable);
            selection.setSelected(enable);
        }

        public Hdd getHdd() {
            return hdds.getSelected();
        }

        public boolean isEnabled() {
            return this.selection.isSelected();
        }
    }

    private static class OddController {

        private ComboBoxController<Odd> odds;

        private JCheckBox selection;

        public OddController(final JComboBox oddBox, final JCheckBox selection) {
            oddBox.setRenderer(new NamedEnumCellRenderer());
            odds = new ComboBoxController<>(oddBox, Odd.values());
            this.selection = selection;
            this.selection.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setEnable(selection.isSelected());
                }
            ;
            });
            setOdd(Odd.DVD_SUPER_MULTI);
            setEnable(false);
        }

        public final void setOdd(Odd odd) {
            if ( odd == null ) return;
            odds.setSelected(odd);
            setEnable(true);
        }

        public final void setEnable(boolean enable) {
            odds.setEnabled(enable);
            selection.setSelected(enable);
        }

        public Odd getOdd() {
            return odds.getSelected();
        }

        public boolean isEnabled() {
            return this.selection.isSelected();
        }
    }

    private static class CpuGpuCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value instanceof Cpu ) {
                ((JLabel)component).setText(((Cpu)value).getModel());
            }
            if ( value instanceof Gpu ) {
                ((JLabel)component).setText(((Gpu)value).getModel());
            }
            return component;
        }
    }

    private static class HddSizeCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ( value instanceof Hdd ) {
                ((JLabel)component).setText(Integer.toString(((Hdd)value).getSize()));
            }
            return component;
        }
    }

    private List<Cpu> allCpus;

    private ComboBoxController<Cpu.Series> cpuSeries;

    private ComboBoxController<Cpu.Manufacturer> cpuManufacturers;

    private ButtonGroupController<Cpu.Type> cpuTypes;

    private ComboBoxController<Gpu.Series> gpuSeries;

    private ComboBoxController<Gpu.Manufacturer> gpuManufacturers;

    private ButtonGroupController<Gpu.Type> gpuTypes;

    private List<Gpu> allGpus;

    private BasicView basicView;

    private Desktop.OsCategory osCategory;

    private HddController hdd1;

    private HddController hdd2;

    private HddController hdd3;

    private HddController hdd4;

    private OddController odd1;

    private OddController odd2;

    private SpecAgent specAgent;

    private Comparator<Cpu> cpuComparator = new Comparator<Cpu>() {
        @Override
        public int compare(Cpu o1, Cpu o2) {
            if ( o1 == o2 ) return 0;
            if ( o1 == null ) return -1;
            if ( o2 == null ) return +1;
            if ( o1.getModel() == o2.getModel() ) return 0;
            if ( o1.getModel() == null ) return -1;
            return o1.getModel().compareTo(o2.getModel());
        }
    };

    private Comparator<Gpu> gpuComparator = new Comparator<Gpu>() {
        @Override
        public int compare(Gpu o1, Gpu o2) {
            if ( o1 == o2 ) return 0;
            if ( o1 == null ) return -1;
            if ( o2 == null ) return +1;
            if ( o1.getModel() == o2.getModel() ) return 0;
            if ( o1.getModel() == null ) return -1;
            return o1.getModel().compareTo(o2.getModel());
        }
    };

    public DesktopView() {
        this(Dl.remote().lookup(SpecAgent.class), null);
    }

    public DesktopView(ProductGroup productGroup) {
        this(Dl.remote().lookup(SpecAgent.class), productGroup);
    }

    /**
     * Creates new form DesktopView
     */
    public DesktopView(SpecAgent specAgent, ProductGroup productGroup) {
        this.specAgent = Objects.requireNonNull(specAgent, "SpecAgent must not be null");
        initComponents();
        basicView = new BasicView();
        basicViewPanel.add(basicView, BorderLayout.CENTER);

        allCpus = specAgent.findAll(Cpu.class);
        cpuBox.setRenderer(new CpuGpuCellRenderer());
        cpuManufacturers = new ComboBoxController<>(cpuManufacturerBox, Cpu.Manufacturer.values());
        cpuSeries = new ComboBoxController<>(cpuSeriesBox, Cpu.Series.values());
        cpuSeriesBox.setRenderer(new NamedEnumCellRenderer());
        cpuTypes = new ButtonGroupController<>(cpuDesktopButton, cpuMobileButton, Cpu.Type.DESKTOP, Cpu.Type.MOBILE);

        allGpus = specAgent.findAll(Gpu.class);
        gpuBox.setRenderer(new CpuGpuCellRenderer());
        gpuManufacturers = new ComboBoxController<>(gpuManufacturerBox, Gpu.Manufacturer.values());
        gpuSeries = new ComboBoxController<>(gpuSeriesBox, Gpu.Series.values());
        gpuSeriesBox.setRenderer(new NamedEnumCellRenderer());
        gpuTypes = new ButtonGroupController<>(gpuDekstopButton, gpuMobileButton, Gpu.Type.DESKTOP, Gpu.Type.MOBILE);

        memoryBox.setModel(new DefaultComboBoxModel(Desktop.MEMORY_SIZES));

        hdd1 = new HddController(hddFirstTypeBox, hddFirstSizeBox, hddFirstHddCheck);
        hdd2 = new HddController(hddSecondTypeBox, hddSecondSizeBox, hddSecondHddCheck);
        hdd3 = new HddController(hddThirdTypeBox, hddThirdSizeBox, hddThirdHddCheck);
        hdd4 = new HddController(hddFourthTypeBox, hddFourthSizeBox, hddFourthHddCheck);

        odd1 = new OddController(oddFirstBox, oddFirstCheck);
        odd2 = new OddController(oddSecondBox, oddSecondCheck);

        miscButton.setActionCommand(Desktop.OsCategory.MISC.toString());
        win7Button.setActionCommand(Desktop.OsCategory.WINDOWS_7.toString());
        win8Button.setActionCommand(Desktop.OsCategory.WINDOWS_8.toString());
        win10Button.setActionCommand(Desktop.OsCategory.WINDOWS_10.toString());
        osBox.setRenderer(new NamedEnumCellRenderer());
        setOs(Os.WINDOWS_7_HOME_PREMIUM_64);
        if ( productGroup == ProductGroup.NOTEBOOK || productGroup == ProductGroup.TABLET_SMARTPHONE ) {
            cpuTypes.setSelected(Cpu.Type.MOBILE);
            gpuTypes.setSelected(Gpu.Type.MOBILE);
        } else {
            cpuTypes.setSelected(Cpu.Type.DESKTOP);
            gpuTypes.setSelected(Gpu.Type.DESKTOP);
        }
        filterCpuSeries();
        filterGpuSeries();
    }

    @Override
    public void setSpec(Desktop desktop) {
        setCpu(desktop.getCpu());
        setGpu(desktop.getGpu());
        setHdds(desktop.getHdds());
        setOdds(desktop.getOdds());
        memoryBox.setSelectedItem(desktop.getMemory());
        setOs(desktop.getOs());
        basicView.setSpec(desktop);
    }

    @Override
    public Desktop getSpec() {
        Desktop desktop = (Desktop)basicView.getSpec();
        desktop.setCpu(getCpu());
        desktop.setGpu(getGpu());
        desktop.setHdds(getHdds());
        desktop.setOdds(getOdds());
        desktop.setMemory((Integer)memoryBox.getSelectedItem());
        desktop.setOs((Os)osBox.getSelectedItem());
        return desktop;
    }

    public BasicView getBasicView() {
        return basicView;
    }

    public void setCpu(Cpu cpu) {
        if ( cpu == null ) return;
        cpuManufacturers.setSelected(cpu.getManufacturer());
        cpuTypes.setSelected(cpu.getTypes().iterator().next());
        cpuSeries.setSelected(cpu.getSeries());
        cpuBox.setSelectedItem(cpu);
    }

    public Cpu getCpu() {
        return (Cpu)cpuBox.getSelectedItem();
    }

    public void setGpu(Gpu gpu) {
        if ( gpu == null ) return;
        gpuManufacturers.setSelected(gpu.getManufacturer());
        gpuTypes.setSelected(gpu.getTypes().iterator().next());
        gpuSeries.setSelected(gpu.getSeries());
        gpuBox.setSelectedItem(gpu);
    }

    public Gpu getGpu() {
        return (Gpu)gpuBox.getSelectedItem();
    }

    public void setHdds(List<Hdd> hdds) {
        hdd1.setEnable(false);
        hdd2.setEnable(false);
        hdd3.setEnable(false);
        hdd4.setEnable(false);
        if ( hdds == null || hdds.isEmpty() ) return;
        if ( hdds.size() >= 1 ) hdd1.setHdd(hdds.get(0));
        if ( hdds.size() >= 2 ) hdd2.setHdd(hdds.get(1));
        if ( hdds.size() >= 3 ) hdd3.setHdd(hdds.get(2));
        if ( hdds.size() >= 4 ) hdd4.setHdd(hdds.get(3));
    }

    public List<Hdd> getHdds() {
        List<Hdd> hdds = new ArrayList<>();
        if ( hdd1.isEnabled() ) hdds.add(hdd1.getHdd());
        if ( hdd2.isEnabled() ) hdds.add(hdd2.getHdd());
        if ( hdd3.isEnabled() ) hdds.add(hdd3.getHdd());
        if ( hdd4.isEnabled() ) hdds.add(hdd4.getHdd());
        return hdds;
    }

    public void setOdds(List<Odd> odds) {
        odd1.setEnable(false);
        odd2.setEnable(false);
        if ( odds == null || odds.isEmpty() ) return;
        if ( odds.size() >= 1 ) odd1.setOdd(odds.get(0));
        if ( odds.size() >= 2 ) odd2.setOdd(odds.get(1));
    }

    public List<Odd> getOdds() {
        List<Odd> odds = new ArrayList<>();
        if ( odd1.isEnabled() ) odds.add(odd1.getOdd());
        if ( odd2.isEnabled() ) odds.add(odd2.getOdd());
        return odds;
    }

    private void filterCpuSeries() {
        cpuSeries.replaceElements(cpuManufacturers.getSelected().getSeries());
        filterCpuNames();
    }

    private void filterCpuNames() {
        List<Cpu> filteredCpus = new ArrayList<>();
        for (Cpu cpu : allCpus) {
            if ( cpu.getTypes().contains(cpuTypes.getSelected()) && cpuSeries.getSelected() == cpu.getSeries() )
                filteredCpus.add(cpu);
        }
        Collections.sort(filteredCpus, cpuComparator);
        cpuBox.setModel(new DefaultComboBoxModel(filteredCpus.toArray()));
    }

    private void filterGpuSeries() {
        gpuSeries.replaceElements(gpuManufacturers.getSelected().getSeries());
        filterGpuNames();
    }

    private void filterGpuNames() {
        List filteredGpus = new ArrayList<>();
        for (Gpu gpu : allGpus) {
            if ( gpu.getTypes().contains(gpuTypes.getSelected()) && gpuSeries.getSelected() == gpu.getSeries() )
                filteredGpus.add(gpu);
        }
        Collections.sort(filteredGpus, gpuComparator);
        gpuBox.setModel(new DefaultComboBoxModel(filteredGpus.toArray()));
    }

    public JPanel getDisplayViewPanel() {
        return displayViewPanel;
    }

    private void setOs(Os os) {
        if ( os == null ) return;
        switch (os.getCategory()) {
            case MISC:
                miscButton.setSelected(true);
                break;
            case WINDOWS_8:
                win8Button.setSelected(true);
                break;
            case WINDOWS_7:
                win7Button.setSelected(true);
                break;
            case WINDOWS_10:
                win10Button.setSelected(true);
                break;
        }
        osBox.setModel(new DefaultComboBoxModel(os.getCategory().getOss()));
        osBox.setSelectedItem(os);
    }

    @Override
    public long getGtin() {
        return basicView.getGtin();
    }

    @Override
    public void setGtin(long gtin) {
        basicView.setGtin(gtin);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        osVersionGroup = new javax.swing.ButtonGroup();
        cpuTypeGroup = new javax.swing.ButtonGroup();
        gpuTypeGroup = new javax.swing.ButtonGroup();
        cpuPanel = new javax.swing.JPanel();
        cpuManufacturerBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cpuBox = new javax.swing.JComboBox();
        cpuDesktopButton = new javax.swing.JRadioButton();
        cpuMobileButton = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        cpuSeriesBox = new javax.swing.JComboBox();
        cpuButtonPanel = new javax.swing.JPanel();
        createCpuButton = new javax.swing.JButton();
        editCpuButton = new javax.swing.JButton();
        hddPanel = new javax.swing.JPanel();
        hddFirstTypeBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        hddFirstHddCheck = new javax.swing.JCheckBox();
        hddSecondTypeBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        hddThirdTypeBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        hddSecondHddCheck = new javax.swing.JCheckBox();
        hddThirdHddCheck = new javax.swing.JCheckBox();
        hddFirstSizeBox = new javax.swing.JComboBox();
        hddSecondSizeBox = new javax.swing.JComboBox();
        hddThirdSizeBox = new javax.swing.JComboBox();
        hddFourthTypeBox = new javax.swing.JComboBox();
        hddFourthSizeBox = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        hddFourthHddCheck = new javax.swing.JCheckBox();
        oddPanel = new javax.swing.JPanel();
        oddFirstBox = new javax.swing.JComboBox();
        oddSecondBox = new javax.swing.JComboBox();
        oddFirstCheck = new javax.swing.JCheckBox();
        oddSecondCheck = new javax.swing.JCheckBox();
        ramPanel = new javax.swing.JPanel();
        memoryBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        gpuPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        gpuManufacturerBox = new javax.swing.JComboBox();
        gpuBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        gpuDekstopButton = new javax.swing.JRadioButton();
        gpuMobileButton = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        gpuSeriesBox = new javax.swing.JComboBox();
        gpuButtonPanel = new javax.swing.JPanel();
        createGpuButton = new javax.swing.JButton();
        editGpuButton = new javax.swing.JButton();
        osPanel = new javax.swing.JPanel();
        osBox = new javax.swing.JComboBox();
        win8Button = new javax.swing.JRadioButton();
        win7Button = new javax.swing.JRadioButton();
        miscButton = new javax.swing.JRadioButton();
        win10Button = new javax.swing.JRadioButton();
        basicViewPanel = new javax.swing.JPanel();
        displayViewPanel = new javax.swing.JPanel();

        cpuPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "CPU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N
        cpuPanel.setOpaque(false);
        cpuPanel.setLayout(new java.awt.GridBagLayout());

        cpuManufacturerBox.setNextFocusableComponent(cpuSeriesBox);
        cpuManufacturerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpuManufacturerBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        cpuPanel.add(cpuManufacturerBox, gridBagConstraints);

        jLabel5.setText("Hersteller:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        cpuPanel.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Typ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        cpuPanel.add(jLabel6, gridBagConstraints);

        cpuBox.setNextFocusableComponent(memoryBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        cpuPanel.add(cpuBox, gridBagConstraints);

        cpuTypeGroup.add(cpuDesktopButton);
        cpuDesktopButton.setText("Desktop");
        cpuDesktopButton.setFocusable(false);
        cpuDesktopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpuDesktopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        cpuPanel.add(cpuDesktopButton, gridBagConstraints);

        cpuTypeGroup.add(cpuMobileButton);
        cpuMobileButton.setText("Mobile");
        cpuMobileButton.setFocusable(false);
        cpuMobileButton.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        cpuMobileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpuMobileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        cpuPanel.add(cpuMobileButton, gridBagConstraints);

        jLabel9.setText("Serie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        cpuPanel.add(jLabel9, gridBagConstraints);

        cpuSeriesBox.setNextFocusableComponent(cpuBox);
        cpuSeriesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpuSeriesBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        cpuPanel.add(cpuSeriesBox, gridBagConstraints);

        createCpuButton.setText("Neu");
        createCpuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCpuButtonActionPerformed(evt);
            }
        });
        cpuButtonPanel.add(createCpuButton);

        editCpuButton.setText("Bearbeiten");
        editCpuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCpuButtonActionPerformed(evt);
            }
        });
        cpuButtonPanel.add(editCpuButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        cpuPanel.add(cpuButtonPanel, gridBagConstraints);

        hddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "HDD", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N
        hddPanel.setMinimumSize(new java.awt.Dimension(783, 104));
        hddPanel.setOpaque(false);

        jLabel1.setText("GB");

        hddFirstHddCheck.setText("1. HDD");
        hddFirstHddCheck.setFocusable(false);
        hddFirstHddCheck.setRolloverEnabled(false);

        hddSecondTypeBox.setEnabled(false);

        jLabel2.setText("GB");

        hddThirdTypeBox.setEnabled(false);

        jLabel3.setText("GB");

        hddSecondHddCheck.setText("2. HDD");

        hddThirdHddCheck.setText("3. HDD");
        hddThirdHddCheck.setNextFocusableComponent(oddFirstBox);

        hddFirstSizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        hddSecondSizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        hddSecondSizeBox.setEnabled(false);

        hddThirdSizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        hddThirdSizeBox.setEnabled(false);

        hddFourthSizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel11.setText("GB");

        hddFourthHddCheck.setText("4. HDD");
        hddFourthHddCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hddFourthHddCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout hddPanelLayout = new javax.swing.GroupLayout(hddPanel);
        hddPanel.setLayout(hddPanelLayout);
        hddPanelLayout.setHorizontalGroup(
            hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hddPanelLayout.createSequentialGroup()
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(hddThirdTypeBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hddSecondTypeBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hddFirstTypeBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 106, Short.MAX_VALUE)
                    .addComponent(hddFourthTypeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(hddFirstSizeBox, 0, 110, Short.MAX_VALUE)
                    .addComponent(hddSecondSizeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hddThirdSizeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hddFourthSizeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hddPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hddSecondHddCheck))
                    .addGroup(hddPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hddThirdHddCheck))
                    .addGroup(hddPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hddFirstHddCheck))
                    .addGroup(hddPanelLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hddFourthHddCheck)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        hddPanelLayout.setVerticalGroup(
            hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hddPanelLayout.createSequentialGroup()
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hddFirstTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(hddFirstHddCheck)
                    .addComponent(hddFirstSizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hddSecondTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(hddSecondHddCheck)
                    .addComponent(hddSecondSizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hddThirdTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hddThirdSizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(hddThirdHddCheck)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hddFourthTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hddFourthSizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11))
                    .addComponent(hddFourthHddCheck))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        oddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "Optische Laufwerke", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N
        oddPanel.setMinimumSize(new java.awt.Dimension(783, 77));
        oddPanel.setOpaque(false);

        oddSecondBox.setEnabled(false);

        oddFirstCheck.setText("1. Laufwerk");
        oddFirstCheck.setFocusable(false);

        oddSecondCheck.setText("2. Laufwerk");

        javax.swing.GroupLayout oddPanelLayout = new javax.swing.GroupLayout(oddPanel);
        oddPanel.setLayout(oddPanelLayout);
        oddPanelLayout.setHorizontalGroup(
            oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(oddPanelLayout.createSequentialGroup()
                .addGroup(oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(oddSecondBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(oddFirstBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 208, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(oddFirstCheck)
                    .addComponent(oddSecondCheck))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        oddPanelLayout.setVerticalGroup(
            oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(oddPanelLayout.createSequentialGroup()
                .addGroup(oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oddFirstBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oddFirstCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(oddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oddSecondBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oddSecondCheck)))
        );

        ramPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "RAM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(1, 1, 1))); // NOI18N
        ramPanel.setMinimumSize(new java.awt.Dimension(783, 48));

        memoryBox.setNextFocusableComponent(gpuManufacturerBox);

        jLabel4.setText("MB");

        javax.swing.GroupLayout ramPanelLayout = new javax.swing.GroupLayout(ramPanel);
        ramPanel.setLayout(ramPanelLayout);
        ramPanelLayout.setHorizontalGroup(
            ramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ramPanelLayout.createSequentialGroup()
                .addComponent(memoryBox, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ramPanelLayout.setVerticalGroup(
            ramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ramPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(memoryBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel4))
        );

        gpuPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "Grafikkarte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        gpuPanel.setMinimumSize(new java.awt.Dimension(783, 110));
        gpuPanel.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("Hersteller:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gpuPanel.add(jLabel7, gridBagConstraints);

        gpuManufacturerBox.setNextFocusableComponent(gpuSeriesBox);
        gpuManufacturerBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpuFilterManufacturer(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gpuPanel.add(gpuManufacturerBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gpuPanel.add(gpuBox, gridBagConstraints);

        jLabel8.setText("Typ:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gpuPanel.add(jLabel8, gridBagConstraints);

        gpuTypeGroup.add(gpuDekstopButton);
        gpuDekstopButton.setText("Desktop");
        gpuDekstopButton.setFocusable(false);
        gpuDekstopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpuFilterType(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gpuPanel.add(gpuDekstopButton, gridBagConstraints);

        gpuTypeGroup.add(gpuMobileButton);
        gpuMobileButton.setText("Mobile");
        gpuMobileButton.setFocusable(false);
        gpuMobileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpuFilterType(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gpuPanel.add(gpuMobileButton, gridBagConstraints);

        jLabel10.setText("Serie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gpuPanel.add(jLabel10, gridBagConstraints);

        gpuSeriesBox.setNextFocusableComponent(gpuBox);
        gpuSeriesBox.setPreferredSize(new java.awt.Dimension(35, 25));
        gpuSeriesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gpuFilterSeries(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gpuPanel.add(gpuSeriesBox, gridBagConstraints);

        createGpuButton.setText("Neu");
        createGpuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createGpuButtonActionPerformed(evt);
            }
        });
        gpuButtonPanel.add(createGpuButton);

        editGpuButton.setText("Bearbeiten");
        editGpuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editGpuButtonActionPerformed(evt);
            }
        });
        gpuButtonPanel.add(editGpuButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gpuPanel.add(gpuButtonPanel, gridBagConstraints);

        osPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, new java.awt.Color(204, 204, 255), new java.awt.Color(1, 1, 1)), "Betriebssystem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        osPanel.setMinimumSize(new java.awt.Dimension(783, 76));

        osVersionGroup.add(win8Button);
        win8Button.setText("Windows 8");
        win8Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                osCategoryFilter(evt);
            }
        });

        osVersionGroup.add(win7Button);
        win7Button.setText("Windows 7");
        win7Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                osCategoryFilter(evt);
            }
        });

        osVersionGroup.add(miscButton);
        miscButton.setText("Sonstige");
        miscButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                osCategoryFilter(evt);
            }
        });

        osVersionGroup.add(win10Button);
        win10Button.setText("Windows 10");
        win10Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                osCategoryFilter(evt);
            }
        });

        javax.swing.GroupLayout osPanelLayout = new javax.swing.GroupLayout(osPanel);
        osPanel.setLayout(osPanelLayout);
        osPanelLayout.setHorizontalGroup(
            osPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(osPanelLayout.createSequentialGroup()
                .addGroup(osPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(osBox, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(osPanelLayout.createSequentialGroup()
                        .addComponent(win7Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(win8Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(win10Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(miscButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        osPanelLayout.setVerticalGroup(
            osPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(osPanelLayout.createSequentialGroup()
                .addGroup(osPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(win8Button)
                    .addComponent(win7Button)
                    .addComponent(miscButton)
                    .addComponent(win10Button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(osBox, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        basicViewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        basicViewPanel.setLayout(new java.awt.BorderLayout());

        displayViewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        displayViewPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hddPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(osPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(oddPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gpuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ramPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cpuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(basicViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                    .addComponent(displayViewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cpuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ramPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(displayViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hddPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gpuPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(oddPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(osPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(basicViewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void gpuFilterManufacturer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gpuFilterManufacturer
        filterGpuSeries();
    }//GEN-LAST:event_gpuFilterManufacturer

    private void gpuFilterType(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gpuFilterType
        filterGpuSeries();
    }//GEN-LAST:event_gpuFilterType

    private void gpuFilterSeries(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gpuFilterSeries
        filterGpuNames();
    }//GEN-LAST:event_gpuFilterSeries

    private void osCategoryFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_osCategoryFilter
        osCategory = Desktop.OsCategory.valueOf(evt.getActionCommand());
        osBox.setModel(new DefaultComboBoxModel(osCategory.getOss()));
    }//GEN-LAST:event_osCategoryFilter

    private void createGpuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createGpuButtonActionPerformed
        EditGpuPanel view = new EditGpuPanel(allGpus);
        view.setDefaults(gpuTypes.getSelected(), gpuSeries.getSelected());
        OkCancelDialog<EditGpuPanel> dialog = new OkCancelDialog<>(parent, "Spezifikationen", view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            allGpus = specAgent.findAll(Gpu.class);
            setGpu(view.getGpu());
        }
    }//GEN-LAST:event_createGpuButtonActionPerformed

    private void editGpuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editGpuButtonActionPerformed
        if ( getGpu() == null ) return;
        EditGpuPanel view = new EditGpuPanel(allGpus);
        view.setGpu(getGpu());
        OkCancelDialog<EditGpuPanel> dialog = new OkCancelDialog<>(parent, "Spezifikationen", view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            allGpus = specAgent.findAll(Gpu.class);
            setGpu(view.getGpu());
        }
    }//GEN-LAST:event_editGpuButtonActionPerformed

    private void editCpuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCpuButtonActionPerformed
        if ( getCpu() == null ) return;
        EditCpuPanel view = new EditCpuPanel(allCpus);
        view.setCpu(getCpu());
        OkCancelDialog<EditCpuPanel> dialog = new OkCancelDialog<>(parent, "Spezifikationen", view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            allCpus = specAgent.findAll(Cpu.class);
            setCpu(view.getCpu());
        }
    }//GEN-LAST:event_editCpuButtonActionPerformed

    private void createCpuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCpuButtonActionPerformed
        EditCpuPanel view = new EditCpuPanel(allCpus);
        view.setDefaults(cpuTypes.getSelected(), cpuSeries.getSelected());
        OkCancelDialog<EditCpuPanel> dialog = new OkCancelDialog<>(parent, "Spezifikationen", view);
        dialog.setVisible(true);
        if ( dialog.getCloseType() == CloseType.OK ) {
            allCpus = specAgent.findAll(Cpu.class);
            setCpu(view.getCpu());
        }
    }//GEN-LAST:event_createCpuButtonActionPerformed

    private void cpuSeriesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpuSeriesBoxActionPerformed
        filterCpuNames();
    }//GEN-LAST:event_cpuSeriesBoxActionPerformed

    private void cpuMobileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpuMobileButtonActionPerformed
        filterCpuSeries();
    }//GEN-LAST:event_cpuMobileButtonActionPerformed

    private void cpuDesktopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpuDesktopButtonActionPerformed
        filterCpuSeries();
    }//GEN-LAST:event_cpuDesktopButtonActionPerformed

    private void cpuManufacturerBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpuManufacturerBoxActionPerformed
        filterCpuSeries();
    }//GEN-LAST:event_cpuManufacturerBoxActionPerformed

    private void hddFourthHddCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hddFourthHddCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hddFourthHddCheckActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel basicViewPanel;
    javax.swing.JComboBox cpuBox;
    javax.swing.JPanel cpuButtonPanel;
    javax.swing.JRadioButton cpuDesktopButton;
    javax.swing.JComboBox cpuManufacturerBox;
    javax.swing.JRadioButton cpuMobileButton;
    javax.swing.JPanel cpuPanel;
    javax.swing.JComboBox cpuSeriesBox;
    javax.swing.ButtonGroup cpuTypeGroup;
    javax.swing.JButton createCpuButton;
    javax.swing.JButton createGpuButton;
    javax.swing.JPanel displayViewPanel;
    javax.swing.JButton editCpuButton;
    javax.swing.JButton editGpuButton;
    javax.swing.JComboBox gpuBox;
    javax.swing.JPanel gpuButtonPanel;
    javax.swing.JRadioButton gpuDekstopButton;
    javax.swing.JComboBox gpuManufacturerBox;
    javax.swing.JRadioButton gpuMobileButton;
    javax.swing.JPanel gpuPanel;
    javax.swing.JComboBox gpuSeriesBox;
    javax.swing.ButtonGroup gpuTypeGroup;
    javax.swing.JCheckBox hddFirstHddCheck;
    javax.swing.JComboBox hddFirstSizeBox;
    javax.swing.JComboBox hddFirstTypeBox;
    javax.swing.JCheckBox hddFourthHddCheck;
    javax.swing.JComboBox hddFourthSizeBox;
    javax.swing.JComboBox hddFourthTypeBox;
    javax.swing.JPanel hddPanel;
    javax.swing.JCheckBox hddSecondHddCheck;
    javax.swing.JComboBox hddSecondSizeBox;
    javax.swing.JComboBox hddSecondTypeBox;
    javax.swing.JCheckBox hddThirdHddCheck;
    javax.swing.JComboBox hddThirdSizeBox;
    javax.swing.JComboBox hddThirdTypeBox;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel10;
    javax.swing.JLabel jLabel11;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel4;
    javax.swing.JLabel jLabel5;
    javax.swing.JLabel jLabel6;
    javax.swing.JLabel jLabel7;
    javax.swing.JLabel jLabel8;
    javax.swing.JLabel jLabel9;
    javax.swing.JComboBox memoryBox;
    javax.swing.JRadioButton miscButton;
    javax.swing.JComboBox oddFirstBox;
    javax.swing.JCheckBox oddFirstCheck;
    javax.swing.JPanel oddPanel;
    javax.swing.JComboBox oddSecondBox;
    javax.swing.JCheckBox oddSecondCheck;
    javax.swing.JComboBox osBox;
    javax.swing.JPanel osPanel;
    javax.swing.ButtonGroup osVersionGroup;
    javax.swing.JPanel ramPanel;
    javax.swing.JRadioButton win10Button;
    javax.swing.JRadioButton win7Button;
    javax.swing.JRadioButton win8Button;
    // End of variables declaration//GEN-END:variables

}
