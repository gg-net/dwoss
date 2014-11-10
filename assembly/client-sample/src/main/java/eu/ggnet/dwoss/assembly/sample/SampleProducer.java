package eu.ggnet.dwoss.assembly.sample;

import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.util.ImageFinder;

/**
 * Contains configuration, which is specific to the local client.
 * <p/>
 * @author oliver.guenther
 */
public class SampleProducer {

    @Produces
    private final static ImageFinder sampleImageFinder = new ImageFinder(null);
}
