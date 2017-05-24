package eu.ggnet.dwoss.receipt.itest.support;

import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.util.ImageFinder;

/**
 *
 * @author oliver.guenther
 */
public class ImageFinderProducer {

    @Produces
    private final static ImageFinder localImageFinder = new ImageFinder(null);
}
