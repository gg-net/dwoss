package eu.ggnet.dwoss.assembly.sample;

import eu.ggnet.dwoss.common.AbstractAccessCos;

import java.util.ArrayList;
import java.util.Arrays;

import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.saft.api.AuthenticationException;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;

@ServiceProvider(service = Guardian.class)
public class NullAccessCos extends AbstractAccessCos implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        Operator login;
        login = (user.equals("test")) ? new Operator(user, 1, Arrays.asList(AtomicRight.values()))
                : new Operator(user, 1, new ArrayList<>());
        setRights(login);
    }
}
