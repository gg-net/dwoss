package eu.ggnet.dwoss.progress;

import eu.ggnet.saft.api.progress.IMonitor;

import javax.enterprise.inject.Alternative;

/**
 *
 * @author oliver.guenther
 */
@Alternative
public class NullMonitor implements IMonitor {

    @Override
    public IMonitor start() {
        return this;
    }

    @Override
    public IMonitor finish() {
        return this;
    }

    @Override
    public IMonitor title(String name) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits) {
        return this;
    }

    @Override
    public IMonitor message(String subMessage) {
        return this;
    }

    @Override
    public IMonitor worked(int workunits, String subMessage) {
        return this;
    }

    @Override
    public int getAbsolutRemainingTicks() {
        return 0;
    }

}
