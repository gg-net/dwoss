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

import java.awt.Dimension;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.swing.*;

import eu.ggnet.dwoss.core.widget.cdi.WidgetProducers;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.rights.api.GroupApi;
import eu.ggnet.dwoss.rights.api.UserApi;
import eu.ggnet.dwoss.rights.ui.NewRightsManagementController;
import eu.ggnet.dwoss.rights.ui.cap.NewRightsManagementAction;
import eu.ggnet.saft.core.Saft;
import eu.ggnet.saft.core.UiUtil;
import eu.ggnet.saft.core.impl.Swing;

import tryout.stub.GroupApiStub;
import tryout.stub.UserApiStub;

/**
 * This tryout allows testing the funcionality of the Rights module seperated from the application.
 *
 * @author mirko.schulze
 */
public class NewRightsManagementControllerTryout {
    
    public static void main(String[] args) {
//        cdiFx();
        cdiSwing();
    }
    
//    public static void cdiFx(){
//        SeContainerInitializer ci = SeContainerInitializer.newInstance();
//        ci.addPackages(NewRightsManagementControllerTryout.class);
//        ci.addPackages(WidgetProducers.class);
//        ci.addPackages(true, NewRightsManagementController.class);
//        ci.disableDiscovery();
//        SeContainer container = ci.initialize();
//        Instance<Object> instance = container.getBeanManager().createInstance();
//
//        Saft saft = instance.select(Saft.class).get();
////        saft.addOnShutdown(() -> container.close());
//
//        RemoteDl remote = instance.select(RemoteDl.class).get();
//        remote.add(UserApi.class, new UserApiStub());
//        remote.add(GroupApi.class, new GroupApiStub());
//        
//        Button b = new Button();
//        b.setOnAction(e -> instance.select(NewRightsManagementAction.class).get());
//        
//        Stage s = new Stage();
//        s.setScene(new Scene(b, 200, 100));
//        
//        saft.core(Fx.class).initMain(s);
//    }

    public static void cdiSwing() {
        SeContainerInitializer ci = SeContainerInitializer.newInstance();
        ci.addPackages(NewRightsManagementControllerTryout.class);
        ci.addPackages(WidgetProducers.class);
        ci.addPackages(true, NewRightsManagementController.class);
        ci.disableDiscovery();
        SeContainer container = ci.initialize();
        Instance<Object> instance = container.getBeanManager().createInstance();

        Saft saft = instance.select(Saft.class).get();
        saft.addOnShutdown(() -> container.close());

        RemoteDl remote = instance.select(RemoteDl.class).get();
        remote.add(UserApi.class, new UserApiStub());
        remote.add(GroupApi.class, new GroupApiStub());
        
        JPanel p = new JPanel();
        JButton b = new JButton("Press to close");
        b.setPreferredSize(new Dimension(200, 50));
        b.addActionListener(e -> {
            saft.closeWindowOf(b);
        });

        p.add(new JButton(instance.select(NewRightsManagementAction.class).get()));
        p.add(b);

        JFrame f = UiUtil.startup(() -> p);
        saft.core(Swing.class).initMain(f);
    }
}
