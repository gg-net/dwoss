package eu.ggnet.dwoss.progress;

import javax.enterprise.inject.Alternative;

/**
 * This is an alternative for the IMonitor Producer.
 * Add the following snipplet to your beans.xml under testing resources:
 * <pre>
 * &lt;alternatives&gt;
 *   &lt;class&gt;de.dw.util.progress.ProgressProducerForTests&lt;/class&gt;
 * &lt;/alternatives&gt;
 * </pre>
 * <p/>
 * @author oliver.guenther
 */
@Alternative
public class ProgressProducerForTests extends MonitorFactory {

    @Override
    public SubMonitor newSubMonitor(String title, int workRemaining) {
        return SubMonitor.convert(new NullMonitor());
    }
}
