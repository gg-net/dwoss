/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.common.exception;

import java.awt.Window;
import java.util.Arrays;
import java.util.function.Consumer;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Alert;
import eu.ggnet.saft.core.UiAlert;

/**
 *
 * @author oliver.guenther
 */
public class UserInfoExceptionConsumer implements Consumer<UserInfoException> {

    @Override
    public void accept(UserInfoException ex) {
        Alert.title(ex.getHead()).message(ex.getMessage())
                .parent(Arrays.stream(Window.getWindows()).filter(Window::isActive).findFirst().orElse(null))
                .show(map(ex.getType()));
    }

    private static UiAlert.Type map(UserInfoException.Type t1) {
        switch (t1) {
            case ERROR:
                return UiAlert.Type.ERROR;
            case WARNING:
                return UiAlert.Type.WARNING;
            case INFO:
            default:
                return UiAlert.Type.INFO;
        }
    }

}
