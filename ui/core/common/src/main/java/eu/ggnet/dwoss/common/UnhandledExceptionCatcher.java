package eu.ggnet.dwoss.common;

import java.awt.AWTEvent;
import java.awt.EventQueue;

public class UnhandledExceptionCatcher extends EventQueue {

    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    protected void dispatchEvent(AWTEvent newEvent) {
        try {
            super.dispatchEvent(newEvent);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtil.show(null, e);
        }
    }
}
