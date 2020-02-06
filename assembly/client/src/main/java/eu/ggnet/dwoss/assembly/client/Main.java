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
package eu.ggnet.dwoss.assembly.client;


import javafx.application.Application;

import eu.ggnet.dwoss.assembly.client.support.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Main Startup Class for the Client.
 *
 * @author oliver.guenther
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Evaluate the console paramters
            ConnectionParameter cp = new ConnectionParameter();
            JCommander.newBuilder().addObject(cp).programName(Main.class.getName()).build().parse(args);
            ApplicationConfiguration.initInstance(cp);
            // Continue the start in JavaFx 
            Application.launch(ClientApplication.class);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.out.println();
            e.getJCommander().usage();
            Application.launch(ErrorApplication.class);
        }
    }
}
