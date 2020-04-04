/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.core.widget;

import java.util.Objects;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import static javafx.scene.control.ButtonType.*;

/**
 * Allrounder confirmation Dialog which returns the payload on ok.
 *
 * @author oliver.guenther
 */
public class ConfirmationDialog<T> extends Dialog<T> {

    public ConfirmationDialog(String title, String question, final T payload) {
        Objects.requireNonNull(payload, "Payload is null, not allowed");
        setTitle(title);
        setHeaderText(question);
        getDialogPane().getButtonTypes().addAll(YES, NO, CANCEL);
        setResultConverter((ButtonType bt) -> {
            if ( bt == CANCEL || bt == NO ) return null;
            return payload;
        });

    }

}
