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
package eu.ggnet.dwoss.redtape.ee.interactiveresult;

import eu.ggnet.dwoss.core.common.UserInfoException;

/**
 *
 * @author oliver.guenther
 */
public interface InteractionListener {

    public enum TrueFalseCancel {

        TRUE, FALSE, CANCEL
    }

    /**
     * Notify the listener.
     * <p>
     * @param type    the type of message
     * @param head    the headline
     * @param message the message.
     */
    void notify(String head, String message, UserInfoException.Type type);

    /**
     * Called for a Yes/No action.
     * <p>
     * @param type    the type of message
     * @param head    the head of the message
     * @param message the message body.
     * @return true for yes, false for no.
     */
    boolean interactYesNo(String head, String message, UserInfoException.Type type);

    boolean interactOkCancel(String head, String message, UserInfoException.Type type);

    TrueFalseCancel interactYesNoCancel(String head, String message, UserInfoException.Type type);

}
