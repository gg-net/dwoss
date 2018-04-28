/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.common.ee;

/**
 * Global Style Sheet information which enhance most of the toHtml Methods of classes with a default nice look.
 *
 * @author oliver.guenther
 */
public class Css {

    public static String toStyle() {
        return "<style>"
                + "table, th, td {"
                + "    border: 1px solid black;"
                + "    border-collapse: collapse;"
                + "}"
                + "</style>";
    }

    public static String toHtml5WithStyle(String body) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + toStyle()
                + "</head>"
                + "<body>"
                + body
                + "</body>"
                + "</html>";
    }
}
