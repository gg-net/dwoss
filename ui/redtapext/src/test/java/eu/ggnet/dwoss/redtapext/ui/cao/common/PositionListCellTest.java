package eu.ggnet.dwoss.redtapext.ui.cao.common;

import eu.ggnet.dwoss.redtapext.ui.cao.common.PositionListCell;

import java.awt.GraphicsEnvironment;

import javafx.embed.swing.JFXPanel;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
public class PositionListCellTest {

    @Test
    public void testResources() {
        if ( GraphicsEnvironment.isHeadless() ) return;
        new JFXPanel(); // Not really sure why, but the Java FX Platform activates itself, so this is needed.
        assertNotNull(PositionListCell.loadDownArrow());
        assertNotNull(PositionListCell.loadLeftArrow());
    }

}
