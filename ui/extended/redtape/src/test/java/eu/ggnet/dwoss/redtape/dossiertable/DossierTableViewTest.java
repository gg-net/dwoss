package eu.ggnet.dwoss.redtape.dossiertable;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author pascal.perau
 */
public class DossierTableViewTest {

    @Test
    public void testLoadImage() {
        for (DossierTableController.IMAGE_NAME image_name : DossierTableController.IMAGE_NAME.values()) {
            Assert.assertNotNull(DossierTableController.load(image_name));
        }
    }

}
