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
import java.util.*;

import javax.naming.*;
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
     * Inspects a context namespace based on the prefixes.
     *
     * @param context  the context to inspect, must not be null.
     * @param prefixes the prefixes to be used, at least one must be supplied.
     * @return a list of names of the found namespace.
     */
    public static List<NameClassPair> inspect(Context context, String... prefixes) {
        Objects.requireNonNull(context, "Context must not be null");
        Objects.requireNonNull(prefixes, "At least one prefix must be supplied");

        List<NameClassPair> result = new ArrayList<>();
        for (String prefix : prefixes) {
            try {
                NamingEnumeration<NameClassPair> list = context.list(prefix);
                while (list != null && list.hasMore()) {
                    try {
                        result.add(list.next());
                    } catch (NamingException ex) {
                    }
                }
            } catch (NamingException ex) {
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(externalIpv4Address().getHostAddress());
    }

}
