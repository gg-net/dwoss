package test;


import eu.ggnet.dwoss.core.system.progress.IMonitor;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author AS
 */
public class JUnitMonitor implements IMonitor {

    public JUnitMonitor(int progressBarLength) {
        this.progressBarlength = progressBarLength;
    }

    int progressBarlength;

    int consumed;

    String name;

    String subMessage;

    @Override
    public IMonitor start() {
        return this;
    }

    @Override
    public IMonitor finish() {
        consumed = progressBarlength;
        return this;
    }

    @Override
    public IMonitor title(String name) {
        this.name = name;
        return this;
    }

    @Override
    public IMonitor worked(int workunits) {
        consumed += workunits;
        return this;
    }

    @Override
    public IMonitor message(String subMessage) {
        this.subMessage = subMessage;
        return this;
    }

    @Override
    public IMonitor worked(int workunits, String subMessage) {
        message(subMessage);
        worked(workunits);
        return this;
    }

    @Override
    public int getAbsolutRemainingTicks() {
        return progressBarlength - consumed;
    }

    public void testConsumed(int consumed, int delta) {
        assertEquals((double)consumed, (double)this.consumed, (double)delta);
    }

    public void testRemaining(int remaining, int delta) {
        assertEquals((double)remaining, (double)getAbsolutRemainingTicks(), (double)delta);
    }
}
