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
package eu.ggnet.dwoss.assembly.client.support.exception;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.AlertType;
import eu.ggnet.saft.core.ui.UiParent;

/**
 *
 * @author oliver.guenther
 */
public class UserInfoExceptionConsumer implements BiConsumer<Optional<UiParent>,UserInfoException> {

    private final static Logger L = LoggerFactory.getLogger(UserInfoExceptionConsumer.class);

    @Override
    public void accept(Optional<UiParent> optParent,UserInfoException ex) {
        L.info("UserInfoException {}", ex.getMessage());
        Objects.requireNonNull(optParent, "optParent must not be null").map(p -> Ui.build().parent(p)).orElse(Ui.build())
                .alert()
                .title(ex.getHead())
                .message(ex.getMessage())
                .show(map(ex.getType()));
    }

    private static AlertType map(UserInfoException.Type t1) {
        switch (t1) {
            case ERROR:
                return AlertType.ERROR;
            case WARNING:
                return AlertType.WARNING;
            case INFO:
            default:
                return AlertType.INFO;
        }
    }

}
