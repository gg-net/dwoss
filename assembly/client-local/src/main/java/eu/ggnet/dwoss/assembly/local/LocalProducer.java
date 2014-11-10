package eu.ggnet.dwoss.assembly.local;

import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.util.ImageFinder;

/**
 * Contains configuration, which is specific to the local client.
 * <p/>
 * @author oliver.guenther
 */
public class LocalProducer {

    @Produces
    private final static ImageFinder localImageFinder = new ImageFinder(null);
}
