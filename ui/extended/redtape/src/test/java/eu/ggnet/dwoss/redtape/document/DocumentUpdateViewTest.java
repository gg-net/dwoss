package eu.ggnet.dwoss.redtape.document;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
public class DocumentUpdateViewTest {

    @Test
    public void testResources() {
        assertNotNull(DocumentUpdateView.loadAddCoin());
        assertNotNull(DocumentUpdateView.loadDown());
        assertNotNull(DocumentUpdateView.loadMinus());
        assertNotNull(DocumentUpdateView.loadAddProductBatch());
        assertNotNull(DocumentUpdateView.loadPlus());
        assertNotNull(DocumentUpdateView.loadUp());
    }

}
