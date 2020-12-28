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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.support.executor.ExecutorManager;
import eu.ggnet.dwoss.assembly.client.support.monitor.MonitorManager;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;

/**
 *
 * @author oliver.guenther
 */
public class MonitorTryout {

    public static class MonitorPaneApplication extends Application {

        private SeContainer container;

        public static class MyTask extends Task<Void> {

            @Override
            protected Void call() throws Exception {
                updateMessage("Starting");
                updateProgress(0, 100);
                for (int i = 0; i < 30; i++) {
                    updateProgress(i * 3, 100);
                    updateMessage("Working " + i);
                    Thread.sleep(100);
                }
                updateMessage("Finish");
                updateProgress(100, 100);
                return null;
            }
        };

        @Override
        public void start(Stage primaryStage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.disableDiscovery();
            ci.addPackages(MonitorManager.class);
            ci.addPackages(ExecutorManager.class);
            ci.addPackages(LoggerProducer.class); // core.system. autolog

            container = ci.initialize();
            MonitorManager monitorManager = container.getBeanManager().createInstance().select(MonitorManager.class).get();

            primaryStage.setScene(new Scene(monitorManager.createPane()));
            primaryStage.show();

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    MyTask t = new MyTask();
                    new Thread(t).start();
                    monitorManager.submit(t);
                    Thread.sleep(1000);
                    t = new MyTask();
                    new Thread(t).start();
                    monitorManager.submit(t);
                    Thread.sleep(500);
                    t = new MyTask();
                    new Thread(t).start();
                    monitorManager.submit(t);
                    t = new MyTask();
                    new Thread(t).start();
                    monitorManager.submit(t);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MonitorTryout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();

        }

        @Override
        public void stop() throws Exception {
            container.close();
        }

    }

    public static void main(String[] args) {
        Application.launch(MonitorPaneApplication.class);
    }

}
