package eu.ggnet.dwoss.receipt.product;

import javax.swing.JRadioButton;

/**
 *
 * @author oliver.guenther
 */
public class ButtonGroupController<T> {

    private JRadioButton b1;

    private JRadioButton b2;

    private T t1;

    private T t2;

    public ButtonGroupController(JRadioButton b1, JRadioButton b2, T t1, T t2) {
        this.b1 = b1;
        this.b2 = b2;
        this.t1 = t1;
        this.t2 = t2;
        b1.setSelected(true);
    }

    public void setSelected(T t) {
        if ( t == null ) return;
        if ( t.equals(t1) ) b1.setSelected(true);
        if ( t.equals(t2) ) b2.setSelected(true);
    }

    public T getSelected() {
        if ( b1.isSelected() ) return t1;
        if ( b2.isSelected() ) return t2;
        throw new RuntimeException("No selection, should never happen.");
    }
}