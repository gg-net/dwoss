package eu.ggnet.dwoss.rules;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author oliver.guenther
 */
// TODO: I need an enum which represents: Manufactuers, Owners, Lieferanten, Firmen
public class AcerRules {

    private final static Pattern partNoPattern = Pattern.compile("[A-Z0-9]{2}\\.[A-Z0-9]{5}\\.[A-Z0-9]{3}");

    /**
     * Generates the mfg date out of the Serial.
     * Serial: PPPPPPPPPPWWWSSSSSMMEE
     * <ul>
     * <li>PPPPPPPPPP: Artikelnummer (10 Stellen), in der Preisliste mit Punkten getrennt (XX.YYYYY.ZZZ)</li>
     * <li>WWW: MfG Date, Herstellungs-Datum (3 Stellen) Die erste Stelle beschreibt das Jahr, die hinteren beiden die Kalenderwoche</li>
     * <li>SSSSS: Die "eigentliche" Seriennummer (5 Stellen) Von 00001 bis FFFFF in Hexadezimal (Jede Woche beginnt neu mit 00001)</li>
     * <li>MM: Herstellungs Code (2 Stellen) Je nach Fabrik</li>
     * <li>EE: Eng. Versions Code (2 Stellen),  wenn keine Version angegeben, 00</li>
     * </ul>
     *
     * @param serial the serial
     * @return a date of the mfg or null if unsuccessful.
     */
    public static Date mfgDateFromSerial(String serial) {
        if ( validateSerialWarning(serial) != null ) return null;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int y_serial = Integer.parseInt(serial.substring(10, 11));
        int yyyy_mfg = (year / 10 * 10) + y_serial;
        if ( year % 10 < y_serial ) yyyy_mfg -= 10;

        cal.set(Calendar.YEAR, yyyy_mfg);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(serial.substring(11, 13)) + 1);
        return cal.getTime();
    }

    public static String partNoFromSerial(String serial) {
        if ( validateSerialError(serial) != null ) return null;
        return serial.substring(0, 2) + "." + serial.substring(2, 7) + "." + serial.substring(7, 10);
    }

    public static String validateSerialWarning(String serial) {
        if ( validateSerialError(serial) != null ) return "Seriennummer in Error";
        if ( serial.length() < 22 ) return "Seriennummer zu kurz, länge ist " + serial.length() + " soll aber 22";
        if ( serial.length() > 22 ) return "Seriennummer zu lang, länge ist " + serial.length() + " soll aber 22";
        String ykw = serial.substring(10, 13);
        try {
            Integer.parseInt(ykw);
        } catch (NumberFormatException e) {
            return "Der MFG-\"Jahr, Woche\"-teil der Seriennummer ist keine Zahl (" + ykw + ")";
        }
        return null;
    }

    public static String validateSerialError(String serial) {
        if ( serial == null ) return "Seriennummer is leer";
        if ( serial.contains(" ") ) return "Seriennummer enthält nicht erlaubte Freizeichen";
        if ( !Pattern.matches("^[A-Z0-9]+$", serial) ) return "Seriennummer enthält nicht erlaubte Zeichen (z.b. öüä)";
        if ( serial.length() < 10 ) return "Seriennummer zu kurz";
        return null;
    }

    /**
     * Returns null if the PartNo is valid for the Manufacturer, else a String representing the Error.
     * <p/>
     * @param partNo the partNo to validate.
     * @return null if the PartNo is valid for the Manufacturer, else a String representing the Error.
     */
    public static String validatePartNo(String partNo) {
        if ( partNo == null ) return "PartNo must not be null";
        if ( !partNoPattern.matcher(partNo).matches() ) {
            return "PartNo for Acer is " + partNo + " and does not match XX.YYYYY.ZZZ";
        }
        return null;
    }
}
