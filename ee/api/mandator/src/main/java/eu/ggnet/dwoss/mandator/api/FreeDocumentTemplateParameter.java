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
package eu.ggnet.dwoss.mandator.api;

/**
 * Contains a parameters, which can be use to put free texts into the Document Template.
 * Enum only for compile safety and refactorabily.
 * <p/>
 * @author oliver.guenther
 */
public enum FreeDocumentTemplateParameter {

    /**
     * Terms left of the sum.
     */
    TERMS1,
    /**
     * Terms below the sum, may be merged with other texts.
     */
    TERMS2
}
