/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import java.awt.EventQueue;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.*;

import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.UiCore;

/**
 * Swing Look and Feel Manager.
 * All methods are wrapping as much as possible of the Laf handling.
 * <p>
 * <i>This class is safe as classic Util/Singleton, as it will never be used in jpro</i>
 * </p>
 *
 * @author oliver.guenther
 */
public class LafMenuManager {

    public final static String ACTIVE_LAF = "activeLaf";

    private final static Logger L = LoggerFactory.getLogger(LafMenuItem.class);

    public static class LafMenuItem extends RadioMenuItem {

        public LafMenuItem(final String className) {

            super(className);
            setOnAction((e) -> {
                EventQueue.invokeLater(() -> {
                    try {
                        UIManager.setLookAndFeel(className);
                        Optional.ofNullable(UiCore.getMainFrame()).ifPresent(m -> SwingUtilities.updateComponentTreeUI(m));
                        storeLaf(className);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        L.error("setOnAction(): Error on changing LAF", ex);
                    }
                });
            });

        }
    }

    /**
     * Creates a javafx menu of all available swing lafs.
     * The menuitems are radio buttom menu items and the active
     * laf is selected. A selection of a laf in the menu will result in
     * the global activation of the laf and the storage of the laf in
     * the user preferences.
     *
     * @return a javafx men of all available swing lafs.
     */
    public static Menu createLafMenu() {
        L.debug("createLafMenu() called");
        String active = UIManager.getLookAndFeel().getClass().getName();
        Menu lafMenu = new Menu("Look & Feel (Swing Components Only)");
        ToggleGroup tg = new ToggleGroup();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

            LafMenuItem i = new LafMenuItem(info.getClassName());
            i.setToggleGroup(tg);
            lafMenu.getItems().add(i);
            if ( info.getClassName().equals(active) ) {
                i.setSelected(true);
            }
        }
        return lafMenu;
    }

    /**
     * Loads the laf for the user preferences and tries to set it in the UIManager.
     * If no Laf was stored before or something fails on the setting, only a warning is logged.
     */
    public static void loadAndSetUserLaf() {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(loadLaf());
                L.debug("loadAndSetUserLaf() successful");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                L.warn("loadAndSetUserLaf() could not be set, keeping default. Message {}" + ex.getMessage());
            }
        });
    }

    private static void storeLaf(String className) {
        if ( className == null ) return;
        Preferences p = Preferences.userNodeForPackage(LookAndFeel.class);
        p.put(ACTIVE_LAF, className);
        try {
            p.flush();
        } catch (BackingStoreException ex) {
            L.error("Cound not store Preferences", ex);
        }
    }

    /**
     * Loads the className of the LAF from the Preferences Store.
     *
     * @return the className
     */
    private static String loadLaf() {
        Preferences p = Preferences.userNodeForPackage(LookAndFeel.class);
        return p.get(ACTIVE_LAF, UIManager.getSystemLookAndFeelClassName());
    }

}
