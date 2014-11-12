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
package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.metawidget.inspector.annotation.UiLarge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 *
 * @author oliver.guenther
 */
@Wither
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ListingMailConfiguration implements Serializable {

    @NotNull
    private String fromAddress; // Reconsider if we need that. Wie have a default.

    @NotNull
    private String toAddress;

    @NotNull
    private String subject;

    @UiLarge
    @NotNull
    private String message;

    @NotNull
    private String charset;

    private String signature;

    public String toMessage() {
        return message + (signature == null ? "" : signature);
    }
}
