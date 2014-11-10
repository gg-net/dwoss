package eu.ggnet.dwoss.util;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author oliver.guenther
 */
public class ComboBoxController<T> {

    private JComboBox comboBox;

    public ComboBoxController(JComboBox comboBox, Collection<T> elems) {
        this(comboBox, elems.toArray());
    }

    public ComboBoxController(JComboBox comboBox, Object[] elems) {
        this.comboBox = comboBox;
        this.comboBox.setModel(new DefaultComboBoxModel(elems));
    }

    public T getSelected() {
        return (T)comboBox.getSelectedItem();
    }

    public void setEnabled(boolean enable) {
        comboBox.setEnabled(enable);
    }

    public void setSelected(T t) {
        // TODO: Check all usages, if this doesn't impact anything
        if (t == null) return;
        comboBox.setSelectedItem(t);
    }

    public void replaceElements(Collection<T> elems) {
        T lastSelection = getSelected();
        comboBox.setModel(new DefaultComboBoxModel(elems.toArray()));
        if ( elems.contains(lastSelection) ) comboBox.setSelectedItem(elems);
    }
}