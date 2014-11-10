package eu.ggnet.dwoss.util;

import java.awt.Window;

/**
 *
 * @author oliver.guenther
 */
public interface IView<T extends Window> {
   
    void setParent(T window);
    
}
