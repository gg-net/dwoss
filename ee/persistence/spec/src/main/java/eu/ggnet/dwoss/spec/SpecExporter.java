package eu.ggnet.dwoss.spec;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * Operation to allow the XML Export of multiple ProductSpecs.
 * <p>
 * @author oliver.guenther
 */
@Remote
public interface SpecExporter {

    /**
     * Exports specs to an XML till the supplied amount.
     * <p>
     * @param amount the amount to export
     * @return a FileJacket containing all the found specs.
     */
    FileJacket toXml(int amount);
}
