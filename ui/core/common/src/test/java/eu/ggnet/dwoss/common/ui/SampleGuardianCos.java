package eu.ggnet.dwoss.common.ui;

import eu.ggnet.dwoss.common.ui.AbstractGuardian;
import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

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
