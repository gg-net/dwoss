package eu.ggnet.dwoss.redtape.gsoffice;

import java.util.Date;

import javax.ejb.Remote;

import eu.ggnet.dwoss.util.FileJacket;

/**
 * GsOffice Exporter.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface GsOfficeExporter {

    /**
     * Exports the all Documents in the Range as the specified XML lines.
     * <p/>
     * @param start the starting date
     * @param end   the ending date
     * @return an Xml document, ready for import in GS Office.
     */
    FileJacket toXml(Date start, Date end);
}
