package eu.ggnet.dwoss.util.table;

/**
 *
 */
public class Column<T> {

    private String headline = "";
    private boolean editable = false;
    private int preferredWidth = 10;
    private Class<?> clazz = Object.class;
    private IColumnGetAction get = null;
    private IColumnGetSetAction set = null;

    public Column(String headline, boolean editable, int preferredWidth, Class<?> clazz, IColumnGetAction action) {
        this.headline = headline;
        this.editable = editable;
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        if (action instanceof IColumnGetAction) {
            this.get = (IColumnGetAction) action;
        }
        if (action instanceof IColumnGetSetAction) {
            this.set = (IColumnGetSetAction) action;
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public String getHeadline() {
        return headline;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public Object getValue(int row) {
        return get.getValue(row);
    }

    public void setValue(int row, Object value) {
        if (!isEditable()) return;
        if (set == null) return;
        set.setValue(row, value);
    }

    public Class<?> getColumnClass() {
        return clazz;
    }
}
