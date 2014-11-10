package eu.ggnet.saft.core;

/**
 * Tagging Interface for JComponents to be added to the Toolbar.
 * <p/>
 * @author oliver.guenther
 */
public interface ToolbarComponent {

    /**
     * Should return a value to order the components, the higher the more to the right.
     * <p/>
     * @return a order value.
     */
    int getOrder();
}
