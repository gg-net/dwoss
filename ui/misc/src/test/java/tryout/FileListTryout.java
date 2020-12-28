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
package tryout;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.misc.ui.mc.FileListPane;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class FileListTryout {

    public static class FileListApplication extends Application {

        private SeContainer container;
        
        @Override
        public void start(Stage primaryStage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.disableDiscovery();
            ci.addPackages(FileListPane.class);
            ci.addPackages(GlobalConfig.class);
            container = ci.initialize();

            FileListPane fl = container.getBeanManager().createInstance().select(FileListPane.class).get();
            primaryStage.setScene(new Scene(fl));
            primaryStage.show();
        }

        @Override
        public void stop() throws Exception {
            container.close();
            UiCore.global().shutdown();
        }        

    }
    
    

    public static void main(String[] args) {
        Application.launch(FileListApplication.class);
    }
}
