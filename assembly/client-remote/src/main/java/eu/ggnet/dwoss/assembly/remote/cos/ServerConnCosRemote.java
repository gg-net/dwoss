package eu.ggnet.dwoss.assembly.remote.cos;

import java.util.Objects;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.Server;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = Server.class)
public class ServerConnCosRemote implements Server {

    public static String URL;

    @Override
    public Context getContext() {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, Objects.requireNonNull(URL, "Remote Host URL is null"));
        try {
            return new InitialContext(properties);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }
}
