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

import javax.swing.JButton;
import javax.swing.JPanel;

import javafx.stage.Modality;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.rights.api.GroupApi;
import eu.ggnet.dwoss.rights.api.UserApi;
import eu.ggnet.dwoss.rights.ui.cap.NewRightsManagementAction;
import eu.ggnet.saft.core.UiCore;

import tryout.stub.*;

/**
 * This tryout allows testing the funcionality of the Rights module seperated from the application.
 *
 * @author mirko.schulze
 */
public class NewRightsManagementControllerTryout {

    public static void main(String[] args) {                
        
        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });
        Dl.remote().add(UserApi.class, new UserApiStub());
        Dl.remote().add(GroupApi.class, new GroupApiStub());

        UiCore.startSwing(() -> {
            JPanel main = new JPanel();
            main.add(new JButton(new NewRightsManagementAction()));
            return main;
        });
    }

}
