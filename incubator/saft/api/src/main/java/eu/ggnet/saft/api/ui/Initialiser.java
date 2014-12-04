package eu.ggnet.saft.api.ui;

/**
 * Optional Initialiser for JPanel and Panes, called while the ui setVisible process.
 *
 * @author oliver.guenther
 */
public interface Initialiser {

    /**
     * initialise some code prior set visible.
     */
    void initialise();

    // Hint: May be some post set visible would be nice.
}
