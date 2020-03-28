package test;

import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import eu.ggnet.dwoss.rights.api.Operator;

/**
 *
 * @author oliver.guenther
 */
public class SampleGuardianCos extends AbstractGuardian implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        throw new AuthenticationException("Not Used in Test");
    }

    /**
     *
     */
    @Override
    protected void setRights(Operator dto) {
        super.setRights(dto);
    }

}
