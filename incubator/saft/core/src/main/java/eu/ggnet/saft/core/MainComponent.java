package eu.ggnet.saft.core;

import java.awt.BorderLayout;

/**
 * Tagging Interface for JComponents to be added to the MainWindow.
 * <p/>
 * @author oliver.guenther
 */
public interface MainComponent {

    /**
     * Returns the {@link BorderLayout} style for the location to add to the Main Window.
     * <p/>
     * @return the {@link BorderLayout} style for the location to add to the Main Window.
     */
    String getLayoutHint();
}
