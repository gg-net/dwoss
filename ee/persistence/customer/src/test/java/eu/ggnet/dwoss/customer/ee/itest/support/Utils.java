/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.customer.ee.itest.support;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author oliver.guenther
 */
public class Utils {
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
            tables.stream().map((table) -> {
                L.debug("Truncating Table {}", table);
                return table;
            }).forEachOrdered((table) -> {
                em.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
            });

            List<String> sequences = em.createNativeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'").getResultList();
            sequences.stream().map((sequence) -> {
                L.debug("Resetting Sequence {}", sequence);
                return sequence;
            }).forEachOrdered((sequence) -> {
                em.createNativeQuery("ALTER SEQUENCE " + sequence + " RESTART WITH 1").executeUpdate();
            });
            L.debug("Enabling foraign key constraints");
            em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }

}
