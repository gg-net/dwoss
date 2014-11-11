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
public class HpPartNoSupport implements PartNoSupport {

    /**
     * Example PartNo: DEH2381234
     * <table>
     * <tr>
     * <td>DE</td>
     * <td>Country code</td>
     * <td>DE = Germany</td>
     * </tr>
     * <tr>
     * <td>H</td>
     * <td>Supply code</td>
     * <td> H = Herrenberg </td>
     * </tr>
     * <tr>
     * <td>238</td>
     * <td>Date of manufacture</td>
     * <td>238 = 2002, week 38</td>
     * </tr>
     * <tr>
     * <td>1234</td>
     * <td>Unit number </td>
     * <td>1234 = unit number for that weekly lot</td>
     * </tr>
     * </table>
     */
    public static final String REGEX = "[A-Z]{2}[A-Z]{1}[0-9]{3}[0-9]{4}";

    @Override
    public boolean isValid(String partNo) {
        return violationMessages(partNo) == null;
    }

    @Override
    public String violationMessages(String partNo) {
        if ( partNo == null ) return "PartNo is null";
        if ( !Pattern.matches(REGEX, partNo) ) return "PartNo " + partNo + " does not match the Pattern " + REGEX;
        return null;
    }

    @Override
    public String normalize(String partNo) {
        return partNo;
    }
}
