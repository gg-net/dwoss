package eu.ggnet.dwoss.assembly.local.cos;

import eu.ggnet.saft.api.AuthenticationException;
import eu.ggnet.saft.core.authorisation.Guardian;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.common.AbstractAccessCos;
import eu.ggnet.dwoss.rights.op.Authentication;

import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.saft.core.Client.lookup;

/**
 * Implementation of an IAuthenticator using the GG-Net Imap Server
 */
@ServiceProvider(service = Guardian.class)
public class ImapAccessCos extends AbstractAccessCos implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        try {
            setRights(lookup(Authentication.class).login(user, pass));
        } catch (UserInfoException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }
}
