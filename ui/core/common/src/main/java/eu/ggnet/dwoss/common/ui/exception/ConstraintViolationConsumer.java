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
package eu.ggnet.dwoss.common.ui.exception;

import java.awt.Window;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.ui.DetailDialog;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;
import eu.ggnet.saft.core.ui.SwingSaft;

import static eu.ggnet.saft.core.ui.exception.ExceptionUtil.toStackStrace;

/**
 *
 * @author oliver.guenther
 */
public class ConstraintViolationConsumer implements Consumer<ConstraintViolationException> {

    private final static Logger L = LoggerFactory.getLogger(UserInfoExceptionConsumer.class);

    @Override
    public void accept(ConstraintViolationException ex) {
        L.info("ConstraintViolationException {}", ConstraintViolationFormater.toSingleLine(new HashSet(ex.getConstraintViolations())));
        SwingSaft.run(() -> {
            DetailDialog.show(Arrays.stream(Window.getWindows()).filter(Window::isActive).findFirst().orElse(null),
                    "Validationsfehler", "Fehler bei der Validation", ConstraintViolationFormater.toMultiLine(ex.getConstraintViolations(), true), toStackStrace(ex));
        });
    }

}