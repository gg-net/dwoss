package eu.ggnet.saft.core;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClientTest {

    @Test
    public void testLoadResources() {
        assertThat(Client.loadProperties()).isNotNull();
    }

    public void testLoadWarrningIcon() {
        assertThat(Client.loadWarningIcon()).isNotNull();
    }
}
