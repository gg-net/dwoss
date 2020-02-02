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
package eu.ggnet.dwoss.misc.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.spec.api.SpecApi;
import eu.ggnet.saft.core.Dl;

/**
 * Sources for the About Dialog.
 *
 * @author oliver.guenther
 */
public class AboutSources {

    private final static String ABOUT_DIALOG_PROPERTIES = "aboutDialog.properties";

    public static String debug() {
        return System.getProperties().entrySet().stream()
                .map(e -> e.getKey() + " = " + e.getValue())
                .reduce((one, two) -> one + "\n" + two)
                .orElse("No Properties found");
    }

    public static String info() {

        String text
                = "Copyright 2014" + "\n"
                + "GG-Net GmbH, Oliver Günther" + "\n"
                + "http://gg-net.de/" + "\n"
                + "http://deutschewarenwirtschaft.de/" + "\n"
                + "GPL v3 Lizenz" + "\n\n"
                + "Version: app.version" + "\n"
                + "System: project.version" + "\n\n"
                + "Aktive Api Module: \n"
                + " - Spec: " + Dl.remote().optional(SpecApi.class).map(s -> "Aktiv").orElse("Nicht vorhanden");

        try (InputStream is = loadProperties().openStream()) {
            Properties prop = new Properties();
            prop.load(is);
            for (Object key : prop.keySet()) {
                text = text.replace(key.toString(), prop.getProperty(key.toString()));
            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(AboutSources.class.getName()).warn("info(): Could not load {}", ABOUT_DIALOG_PROPERTIES, ex);
        }
        return text;
    }

    /**
     *
     *
     * @return
     */
    public static URL loadProperties() {
        return AboutSources.class.getResource(ABOUT_DIALOG_PROPERTIES);
    }

}
