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
package eu.ggnet.dwoss.rules;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author oliver.guenther
 */
public class AppleRules {

    private final static Pattern partNoPattern = Pattern.compile("M[A-Z][A-Z0-9]{3}[A-Z]{1,2}/[A-Z]{1}");

    private final static Pattern serialPattern = Pattern.compile("[A-Z0-9]{11,12}");

    /**
     * Validates a PartNo of Apple, returns null if ok or else a String representing the error.
     * Allowed is : M[A-Z][0-9]{3}[A-Z]{1-2}/A
     * <p/>
     * @param partNo the partNo to validate
     * @return null if ok or else a String representing the error.
     */
    public static String validatePartNo(String partNo) {
        if ( partNo == null ) return "PartNo must not be null";
        if ( !partNoPattern.matcher(partNo).matches() ) {
            return "Apple Artikelnummer " + partNo + " passt nicht auf Apple Pattern M?###?/A or M?###??/A (?=Buchstabe, #=Zahl)";
        }
        return null;
    }

    /**
     * Validates a Serial of Apple, returns null if ok or else a String representing the error.
     * Allowed is: [A-Z][A-Z0-9]{11}
     * F17JQM9DDTWD
     * <p/>
     * <
     * p/>
     * @param serial the serial
     * @return null if ok or else a String representing the error.
     */
    public static String validateSerial(String serial) {
        if ( serial == null ) return "Seriennummer darf nicht null sein";
        if ( serial.length() < 11 ) return "Seriennummer ist zu kurz ! (ist " + serial.length() + ", soll 11-12)";
        if ( serial.length() > 12 ) return "Seriennummer ist zu lang ! (ist " + serial.length() + ", soll 11-12)";
        if ( !serialPattern.matcher(serial).matches() ) {
            return "Apple Seriennummer " + serial + " passt nicht auf Apple Pattern ************ (*=Zahl/Buchstabe) !";
        }
        return null;
    }

    /**
     * Generates the mfg date out of the Serial.
     * Serial: AABCCDDDEEF
     * <ul>
     * <li>AA = Factory and machine ID</li>
     * <li>B = Year manufactured (simplified to final digit, 2010 is 0, 2011 is 1, etc)</li>
     * <li>CC = Week of production</li>
     * <li>DDD = Unique identifier (not the same as UDID)</li>
     * <li>EE = Color of device</li>
     * <li>F = Size of storage, S is 16GB and T is 32GB</li>
     * </ul>
     *
     * @param serial the serial
     * @return a date of the mfg or null if unsuccessful.
     */
    public static Date mfgDateFromSerial(String serial) {
        if ( validateSerial(serial) != null ) return null;
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int y_serial = Integer.parseInt(serial.substring(2, 3));
            int yyyy_mfg = (year / 10 * 10) + y_serial;
            if ( year % 10 < y_serial ) yyyy_mfg -= 10;

            cal.set(Calendar.YEAR, yyyy_mfg);
            cal.set(Calendar.DAY_OF_WEEK, 1);
            cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(serial.substring(3, 5)) + 1);
            return cal.getTime();
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

}
