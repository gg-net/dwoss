package test;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;

import eu.ggnet.dwoss.common.ee.log.AutoLoggerInterceptor;

import nu.xom.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class TestBeansXml {

    @Test
    public void testAlternatives() throws ParsingException, ValidityException, IOException {
        // Refactor Safty Net.
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Properties p = new Properties();
        URL url = cl.getResource("support.properties");
        assertThat(url).isNotNull();

        p.load(url.openStream());
        String beans = p.getProperty("beans");
        assertThat(beans).isNotEmpty();
        File f = new File(beans);
        assertThat(f).exists().canRead();

        Builder parser = new Builder();
        try (FileInputStream fi = new FileInputStream(f)) {
            Document doc = parser.build(fi);
            Element rootElement = doc.getRootElement();

            assertThat(rootElement.getLocalName()).isEqualTo("beans");
            assertThat(rootElement.getChildElements().get(0).getLocalName()).isEqualTo("interceptors");
            assertThat(rootElement.getChildElements().get(0).getChildElements().get(0).getLocalName()).isEqualTo("class");
            assertThat(rootElement.getChildElements().get(0).getChildElements().get(0).getValue().trim()).isEqualTo(AutoLoggerInterceptor.class.getName());
        }
    }
}
