package eu.ggnet.dwoss.mandator.api.service;

import java.io.File;
import java.util.*;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.mandator.api.value.partial.ListingMailConfiguration;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.FileJacket;

/**
 *
 * @author pascal.perau
 */
@Local
@Remote
public interface ListingService {

    /**
     * Return a collection of action configurations.
     * <p>
     * @return a collection of action configurations.
     */
    List<ListingActionConfiguration> listingActionConfigurations();

    List<ListingConfiguration> listingConfigurations();

    FtpConfiguration listingFtpConfiguration(Map<TradeName, Collection<FileJacket>> files);
    
    ListingMailConfiguration listingMailConfiguration();
}
