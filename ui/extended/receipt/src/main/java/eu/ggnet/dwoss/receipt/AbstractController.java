package eu.ggnet.dwoss.receipt;

import java.awt.Window;

/**
 * Abstract Controller Class to keep a reference to the view.
 *
 * @author oliver.guenther
 */
public abstract class AbstractController {

    protected Window view;

    public void setView(Window view) {
        this.view = view;
    }

}
