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
package eu.ggnet.dwoss.util;

import java.net.*;
import java.time.*;
import java.util.*;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Misc Utils, split if too much.
 * <p>
 * @author oliver.guenther
 */
public class Utils {

    /**
     * Returns the 1st external ipv4 address.
     * <p>
     * @return the 1st external ipv4 address.
     */
    public static InetAddress externalIpv4Address() {
        try {
            Enumeration<NetworkInterface> netInter = NetworkInterface.getNetworkInterfaces();
            int n = 0;

            while (netInter.hasMoreElements()) {
                NetworkInterface ni = netInter.nextElement();
                for (InetAddress iaddress : Collections.list(ni.getInetAddresses())) {
                    if ( iaddress.isLoopbackAddress() ) continue;
                    if ( !(iaddress instanceof Inet4Address) ) continue;
                    return iaddress;
                }
            }
        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalStateException("No External Ip found, sure u are in the net ?");
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
     * End of day based on Date.
     *
     * @param in input
     * @return a date.
     */
    public static Date endOfDay(Date in) {
        return toDate(toLd(in).atTime(23, 59, 59));
    }
    
    public static void main(String[] args) {
        System.out.println(externalIpv4Address().getHostAddress());
    }

}
