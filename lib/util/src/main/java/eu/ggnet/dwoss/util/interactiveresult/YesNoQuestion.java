/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.util.interactiveresult;

import java.io.Serializable;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 * A Yes No Question for the Result.
 * <p>
 * @author oliver.guenther
 */
public class YesNoQuestion implements Serializable {

    private final String head;

    private final String message;

    private final UserInfoException.Type type;

    private UserInfoException noOption = null;

    public YesNoQuestion(String head, String message, UserInfoException.Type type) {
        this.head = head;
        this.message = message;
        this.type = type;
    }

    /**
     * Enables the {@link UserInfoException} on the no option with the supplied message.
     * <p>
     * @param message the message.
     * @return the {@link UserInfoException} on the no option with the supplied message.
     */
    public YesNoQuestion onNoException(String message) {
        this.noOption = new UserInfoException(message);
        return this;
    }

    public YesNoQuestion onNoException(String head, String message, UserInfoException.Type type) {
        this.noOption = new UserInfoException(head, message, type);
        return this;
    }

    public void ask(InteractionListener listener) throws UserInfoException {
        boolean yes = listener.interactYesNo(head, message, type);
        if ( !yes && noOption != null ) throw noOption;
    }

}
