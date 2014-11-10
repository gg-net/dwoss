package eu.ggnet.dwoss.redtape.reporting;

import javax.ejb.Remote;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface RedTapeCloser {

    public void executeManual(String arranger);

    // called by scheduler.
    public void executeAutomatic();

}
