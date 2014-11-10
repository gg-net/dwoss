package eu.ggnet.dwoss.common;

import eu.ggnet.dwoss.common.AbstractAccessCos;
import eu.ggnet.saft.api.AuthenticationException;
import eu.ggnet.saft.core.authorisation.Guardian;

import eu.ggnet.dwoss.rights.api.Operator;

/**
 *
 * @author oliver.guenther
 */
public class SampleAccessCos extends AbstractAccessCos implements Guardian {

    @Override
    public void login(String user, char[] pass) throws AuthenticationException {
        throw new AuthenticationException("Not Used in Test");
    }

    /**
     *
     * @param sopoRights the value of sopoRights
     */
    @Override
    protected void setRights(Operator dto) {
        super.setRights(dto);
    }

}
