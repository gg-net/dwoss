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
import java.util.Objects;

import lombok.Data;

/**
 * DocumentParameter.
 *
 * @author oliver.guenther
 */
@Data
public class MailDocumentParameter implements Serializable {

    private final String name;

    private final String documentType;

    /**
     * Evaluates this parameter object by replacing the paramters in the template.
     * <p>
     * @param template the template, must not be null
     * @return the final replacement object.
     */
    //TODO: Another worst case solution, but we can life with it.
    public String eval(String template) {
        return Objects.requireNonNull(template, "eval was called with null template")
                .replaceAll("\\$parameter.name", getName())
                .replaceAll("\\$parameter.documentType", getDocumentType());

    }
}
