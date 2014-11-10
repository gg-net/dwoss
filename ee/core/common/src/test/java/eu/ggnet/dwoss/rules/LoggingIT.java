package eu.ggnet.dwoss.rules;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.configuration.SystemConfig;

/**
 *
 * @author oliver.guenther
 */

public class LoggingIT {

    private EJBContainer container;

    @Inject
    private LoggingIT.LoggingService bean;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void after() {
        container.close();
    }

    @Test
    @Ignore
    // This test is only usefull to look up all logging components via human interaction.
    public void testIfLoggingIsWorking() {
        bean.executeLog();
    }

    @Stateless
    public static class LoggingService {

        private Logger L = LoggerFactory.getLogger(LoggingService.class);

        public void executeLog() {
            System.out.println("Logging Executed");
            L.error("An Error");
            L.warn("A Warning");
            L.info("An Info");
            L.debug("A Debug");
            L.trace("Some Trace");
        }
    }
}
