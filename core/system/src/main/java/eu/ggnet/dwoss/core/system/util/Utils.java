/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.core.system.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Misc Utils, split if too much.
 * <p>
 * @author oliver.guenther
 */
public class Utils {

    private final static Logger L = LoggerFactory.getLogger(Utils.class);

    private final static String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private final static String USER_HOME = "user.home";

    private final static String OS_NAME = "os.name";

    /**
     * SimpleDateFormat("yyyy-MM-dd").
     */
    public static final DateFormat ISO_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private static File tryPath(File path, String subPath) {
        L.debug("tryPath(path={},subPath={}) path.isDirectory()={}, path.canWrite()={}", path, subPath, path.isDirectory(), path.canWrite());
        if ( path.isDirectory() && path.canWrite() ) {
            File outputPath = new File(path, subPath);
            if ( !outputPath.exists() ) {
                if ( !outputPath.mkdirs() ) {
                    L.debug("tryPath() outpath={} didn't exist, but mkdirs() was not successful. Returning null", outputPath);
                    return null;
                }
            } else if ( !(outputPath.isDirectory() && outputPath.canWrite()) ) {
                L.debug("tryPath() outpath={} exist, but not accessable. Returning null", outputPath);
                return null;
            }
            L.debug("tryPath() success. Returning outpath={}", outputPath);
            return outputPath;
        }
        L.debug("tryPath() path={} not accessable. Returning null", path);
        return null;
    }

    /**
     * Tryies to find a location for Temporary files. Optionaly creates the supplied name as Directory an returns a handle.
     *
     * @param name the desired name of the directory
     * @return a handle to a temp directory
     * @throws RuntimeException if somthing goes wrong.
     */
    public static File getTempDirectory(String name) throws RuntimeException {
        L.debug("getTempDirectory(name={})", name);
        File outputPath = null;
        if ( System.getProperty(JAVA_IO_TMPDIR) != null )
            outputPath = tryPath(new File(System.getProperty(JAVA_IO_TMPDIR)), name);
        if ( outputPath == null )
            outputPath = tryPath(new File(System.getProperty(USER_HOME)), "Temp/" + name);
        if ( outputPath == null ) {
            if ( System.getProperty(OS_NAME).startsWith("Windows") ) {
                outputPath = tryPath(new File("C:/"), "Temp/" + name);
                if ( outputPath == null ) outputPath = tryPath(new File("D:/"), "Temp/" + name);
            }
        }
        if ( outputPath == null ) throw new RuntimeException("No usable Templocation found, giving up");
        return outputPath;
    }

    /**
     * Clears the content of one or more H2 database, keeping the structure and resetting all sequences.
     * This method uses H2 native SQL commands. Tran
     *
     * @param ems the entitymanager of the database.
     */
    public static void clearH2Db(EntityManager... ems) {
        final Logger L = LoggerFactory.getLogger(Utils.class);
        if ( ems == null ) {
            L.info("No entitymanagers supplierd, ignoring clear");
            return;
        }

        for (EntityManager em : ems) {
            L.info("Clearing EntityManager {}", em);
            L.debug("Disabing foraign key constraints");
            em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

            List<String> tables = em.createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'").getResultList();
            for (String table : tables) {
                L.debug("Truncating Table {}", table);
                em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
            }

            List<String> sequences = em.createNativeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'").getResultList();
            for (String sequence : sequences) {
                L.debug("Resetting Sequence {}", sequence);
                em.createNativeQuery("ALTER SEQUENCE " + sequence + " RESTART WITH 1").executeUpdate();
            }
            L.debug("Enabling foraign key constraints");
            em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }

    /**
     * Converts a Date to a LocalDateTime based on the SystemTimeZone.
     * If an java.sql.Date is supplied, its converted to locaDate.atStartOfDay().
     * If an java.sql.Time is supplied, its converted to localTime.atDate(LocalDate.now());
     *
     * @param in the date to convert.
     * @return a LocalDateTime instance or null if date was null.
     */
    public static LocalDateTime toLdt(Date in) {
        if ( in == null ) return null;
        if ( in instanceof java.sql.Date ) {
            return ((java.sql.Date)in).toLocalDate().atStartOfDay();
        }
        return LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Converts a Date to a LocalDate based on the SystemTimeZone.
     * If an java.sql.Date is supplied, its converted via toLocalDate;
     *
     * @param in the date to convert
     * @return a LocalDate instance or null if date was null;
     */
    public static LocalDate toLd(Date in) {
        if ( in == null ) return null;
        if ( in instanceof java.sql.Date ) {
            return ((java.sql.Date)in).toLocalDate();
        }
        return in.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts a LocalTime to a LocalDate based on the SystemTimeZone.
     * If an java.sql.Date is supplied, its converted to a localTime 00:00
     *
     * @param in the date to convert
     * @return a LocalDate instance or null if date was null;
     */
    public static LocalTime toLt(Date in) {
        if ( in == null ) return null;
        if ( in instanceof java.sql.Date ) {
            return ((java.sql.Date)in).toLocalDate().atStartOfDay().toLocalTime();
        } else if ( in instanceof java.sql.Time ) {
            return ((java.sql.Time)in).toLocalTime();
        }
        return in.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * Converts a LocalDate to a Date.
     *
     * @param in the localDate to convert.
     * @return a Date
     */
    public static Date toDate(LocalDate in) {
        if ( in == null ) return null;
        return Date.from(in.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts a LocalDateTime to a Date.
     *
     * @param in the localDate to convert.
     * @return a Date
     */
    public static Date toDate(LocalDateTime in) {
        if ( in == null ) return null;
        return Date.from(in.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Start of day based on Date.
     *
     * @param in input
     * @return returns a date.
     */
    public static Date startOfDay(Date in) {
        return toDate(toLd(in).atStartOfDay());
    }

    /**
     * Verifys a string, if it's not blank. Throws an exception, if not valid. It's like Objects.requireNonNull().
     *
     * @param value   the value to verify
     * @param message an optional
     * @return the value
     * @throws IllegalArgumentException if value is null or blank.
     */
    public static String requireNonBlank(String value, String message) throws IllegalArgumentException {
        String em = message == null ? "" + value + " is blank, not allowed" : message;
        if ( isBlank(value) ) throw new IllegalArgumentException(em);
        return value;
    }

    /**
     * Taken from apache commons StringUtils
     * Checks if a CharSequence is empty (""), null or whitespace only.
     * <p>
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     * <pre>
     * StringUtils.isBlank(null) = true
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true
     * StringUtils.isBlank("bob") = false
     * StringUtils.isBlank(" bob ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if ( cs == null || (strLen = cs.length()) == 0 ) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ( !Character.isWhitespace(cs.charAt(i)) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * End of day based on Date.
     *
     * @param in input
     * @return a date.
     */
    public static Date endOfDay(Date in) {
        return toDate(toLd(in).atTime(23, 59, 59));
    }

    /**
     * Formats supplied long to a gtin-13 String, fillig the supplied value with zeros.
     *
     * @param gtin the gtin.
     * @return a gtin-13 format.
     */
    public static String toGtin13(long gtin) {
        return String.format("%013d", gtin);
    }

}
