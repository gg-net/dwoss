package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import eu.ggnet.dwoss.core.system.autolog.AutoLoggerInterceptor;
import eu.ggnet.dwoss.core.system.progress.ProgressProducerForTests;

import nu.xom.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class TestBeansXml {

    /**
     * Verifies, that in the beans.xml the interceptor class is correctly configured.
     *
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    @Test
    public void verifyEnabledInterceptor() throws ParsingException, ValidityException, IOException {
        URL beansXml = AutoLoggerInterceptor.class.getClassLoader().getResource("META-INF/beans.xml");
        assertThat(beansXml).as("beans.xml should be on the classpath").isNotNull();

        Builder parser = new Builder();
        try (InputStream fi = beansXml.openStream()) {
            Document doc = parser.build(fi);
            Element rootElement = doc.getRootElement();

            assertThat(rootElement.getLocalName()).isEqualTo("beans");
            assertThat(rootElement.getChildElements().get(0).getLocalName()).isEqualTo("interceptors");
            assertThat(rootElement.getChildElements().get(0).getChildElements().get(0).getLocalName()).isEqualTo("class");
            assertThat(rootElement.getChildElements().get(0).getChildElements().get(0).getValue().trim()).isEqualTo(AutoLoggerInterceptor.class.getName());
        }
    }

    @Test
    public void verifyAlternativesInTestBeans() throws ParsingException, ValidityException, IOException {
        // Refactor Safty Net.
        URL beansXml = ClassLoader.getSystemClassLoader().getResource("META-INF/test-beans.xml");
        assertThat(beansXml).isNotNull();

        try (InputStream fi = beansXml.openStream()) {
            Builder parser = new Builder();
            Document doc = parser.build(fi);
            Element rootElement = doc.getRootElement();

            assertThat(extract(rootElement.getChildElements(), "alternatives")).as("Alternatives in test-beans.xml").isNotNull().extracting(e -> e.getValue().trim()).isEqualTo(ProgressProducerForTests.class.getName());
        }
    }

    // Info: getFirstChildElements does not work.
    private Element extract(Elements es, String name) {
        for (int i = 0; i < es.size(); i++) {
            Element e = es.get(i);
            if ( e.getLocalName().equals(name) ) return e;
        }
        return null;
    }

}
