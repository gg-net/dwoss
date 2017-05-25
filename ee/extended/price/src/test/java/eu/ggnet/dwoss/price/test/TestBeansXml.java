package eu.ggnet.dwoss.price.test;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import eu.ggnet.dwoss.progress.ProgressProducerForTests;

import nu.xom.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class TestBeansXml {

    @Test
    public void testAlternatives() throws ParsingException, ValidityException, IOException {
        // Refactor Safty Net.
        URL url = ClassLoader.getSystemClassLoader().getResource("META-INF/beans.xml");
        assertThat(url).isNotNull();

        Builder parser = new Builder();
        Document doc = parser.build(url.openStream());
        Element rootElement = doc.getRootElement();
        assertThat(rootElement.getChildElements().get(0).getLocalName())
                .isEqualTo("alternatives");
        assertThat(rootElement.getChildElements().get(0).getValue().trim())
                .isEqualTo(ProgressProducerForTests.class.getName());

    }

}
