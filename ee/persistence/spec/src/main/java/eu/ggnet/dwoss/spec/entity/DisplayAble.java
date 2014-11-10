package eu.ggnet.dwoss.spec.entity;

import eu.ggnet.dwoss.spec.entity.piece.Display;

/**
 * Represents something containing a Display.
 *
 * @author oliver.guenther
 */
public interface DisplayAble {

    Display getDisplay();

    void setDisplay(Display display);

}
