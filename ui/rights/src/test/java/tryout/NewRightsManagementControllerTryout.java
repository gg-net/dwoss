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

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;
import eu.ggnet.dwoss.rights.ee.GroupAgent;
import eu.ggnet.dwoss.rights.ee.UserAgent;
import eu.ggnet.dwoss.rights.ui.cap.NewRightsManagementAction;
import eu.ggnet.saft.core.UiCore;

import tryout.stub.GroupAgentStub;
import tryout.stub.UserAgentStub;

/**
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
        Dl.remote().add(UserAgent.class, new UserAgentStub());
        Dl.remote().add(GroupAgent.class, new GroupAgentStub());

        UiCore.startSwing(() -> {

            JPanel main = new JPanel();
            main.add(new JButton(new NewRightsManagementAction()));
            return main;
        });
    }

}
