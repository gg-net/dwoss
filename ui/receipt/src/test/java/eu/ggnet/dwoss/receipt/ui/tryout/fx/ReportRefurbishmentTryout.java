/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.fx;

import java.util.Set;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.FileUtil;
import eu.ggnet.dwoss.core.widget.Progressor;
import eu.ggnet.dwoss.core.widget.auth.*;
import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.LocalDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.receipt.ee.reporting.RefurbishmentReporter;
import eu.ggnet.dwoss.receipt.ui.ReportRefurbishmentController;
import eu.ggnet.dwoss.receipt.ui.cap.ReportRefurbishmentMenuItem;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.RefurbishmentReporterStub;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.impl.Fx;

/**
 *
 * @author mirko.schulze
 */
public class ReportRefurbishmentTryout {

    public static class ReportRefurbishmentApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer ci = SeContainerInitializer.newInstance();
            ci.addPackages(WidgetProducers.class);
            ci.addPackages(true, ReportRefurbishmentTryout.class);
            ci.addPackages(ReportRefurbishmentController.class);
            ci.addPackages(ReportRefurbishmentMenuItem.class);
            ci.addPackages(FileUtil.class);
            ci.addPackages(Progressor.class);
            ci.disableDiscovery();
            SeContainer container = ci.initialize();
            Instance<Object> instance = container.getBeanManager().createInstance();

            Saft saft = instance.select(Saft.class).get();
            UiCore.initGlobal(saft);
            saft.addOnShutdown(() -> container.close());

            LocalDl local = instance.select(LocalDl.class).get();
            local.add(Guardian.class, new Guardian() {

                @Override
                public String getUsername() {
                    return "Demo";
                }

                //<editor-fold defaultstate="collapsed" desc="unused">
                @Override
                public void logout() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Set<String> getOnceLoggedInUsernames() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Set<String> getAllUsernames() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void login(String user, char[] pass) throws AuthenticationException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public Set<AtomicRight> getRights() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean quickAuthenticate(int userId) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void remove(Object instance) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void addUserChangeListener(UserChangeListener listener) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void removeUserChangeListener(UserChangeListener listener) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void add(Accessable accessable) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void add(Object enableAble, AtomicRight authorisation) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void remove(Accessable accessable) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean hasRight(AtomicRight authorisation) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                //</editor-fold>
            });

            RemoteDl remote = instance.select(RemoteDl.class).get();
            remote.add(RefurbishmentReporter.class, new RefurbishmentReporterStub());

            UiUtil.startup(stage, () -> {
                Menu m = new Menu("Refurbishment");
                m.getItems().addAll(instance.select(ReportRefurbishmentMenuItem.class).get());
                return new MenuBar(m);
            });

            saft.core(Fx.class).initMain(stage);
        }

    }

    public static void main(String[] args) {
        Application.launch(ReportRefurbishmentApplication.class, args);
    }

}
