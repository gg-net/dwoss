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

import javax.validation.ConstraintViolationException;

import eu.ggnet.dwoss.common.DetailDialog;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;
import eu.ggnet.saft.core.swing.SwingSaft;

import static eu.ggnet.saft.core.exception.ExceptionUtil.*;

/**
 *
 * @author oliver.guenther
 */
public class ConstraintViolationConsumer implements Consumer<ConstraintViolationException> {

    @Override
    public void accept(ConstraintViolationException ex) {
        SwingSaft.execute(() -> {
            DetailDialog.show(Arrays.stream(Window.getWindows()).filter(Window::isActive).findFirst().orElse(null),
                    "Validationsfehler", "Fehler bei der Validation", ConstraintViolationFormater.toMultiLine(ex.getConstraintViolations(), true), toStackStrace(ex));
        });
    }

}
