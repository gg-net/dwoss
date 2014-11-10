package eu.ggnet.dwoss.assembly.web.server;

import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.util.ImageFinder;

/**
 * Contains configuration, which is specific to the Server.
 * <p/>
 * @author oliver.guenther
 */
public class ServerProducer {

    @Produces
    private final static ImageFinder serverImageFinder = new ImageFinder("/remote/megatron/Application/DeutscheWarenwirtschaft/images/");
}
