package eu.ggnet.dwoss.util.table;

import eu.ggnet.dwoss.util.PojoUtil;

public class PojoColumn<T> {

    private String headline = "";
    private boolean editable = false;
    private int preferredWidth = 10;
    private Class<?> clazz = Object.class;
    private String propertyName;

    public PojoColumn(String headline, boolean editable, int preferredWidth, Class<?> clazz, String propertyName) {
        this.headline = headline;
        this.editable = editable;
        this.preferredWidth = preferredWidth;
        this.clazz = clazz;
        this.propertyName = propertyName;
    }

    public boolean isEditable() {
        return false;
    }

    public String getHeadline() {
        return headline;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public Object getValue(T elem) {
        return PojoUtil.getValue(propertyName, elem);
    }

    public Class<?> getColumnClass() {
        return clazz;
    }

}
