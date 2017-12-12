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
package eu.ggnet.dwoss.rules.partno;

import java.util.regex.Pattern;

/**
 *
 * @author bastian.venz
 */
public class LenovoPartNoSupport implements PartNoSupport {

    private static final String SHORT_PATTERN = "[0-9]{7}";

    private static final String LONG_PATTERN = "[a-zA-Z]{2}[0-9]{8}";

    @Override
    public boolean isValid(String partNo) {
        return violationMessages(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        if ( !(Pattern.matches(LONG_PATTERN, partNo) || Pattern.matches(SHORT_PATTERN, partNo)) ) {
            return "Part No Don't match patterns " + SHORT_PATTERN + " or " + LONG_PATTERN;
        }
        return null;
    }

}
