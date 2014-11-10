package eu.ggnet.dwoss.util.table;

/**
 * Combinder of Get and set
 */
public interface IColumnGetSetAction extends IColumnGetAction {

     void setValue(int row, Object value);

}
