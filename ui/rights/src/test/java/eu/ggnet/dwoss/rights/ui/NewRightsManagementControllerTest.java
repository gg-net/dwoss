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
package eu.ggnet.dwoss.rights.ui;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.rights.ee.*;

import tryout.stub.*;

import static org.assertj.core.api.Assertions.assertThat;

public class NewRightsManagementControllerTest {

    @Test
    public void testResource() {
        assertThat(RightsManagmentController.loadFxml()).isNotNull();
    }
    
//    @Test
//    public void testJavaFxFxml() throws IOException {
//        if ( GraphicsEnvironment.isHeadless() ) return;
//        new JFXPanel(); // Implizit start of JavaFx.
//        Dl.remote().add(UserAgent.class, new UserAgentStub());
//        Dl.remote().add(GroupAgent.class, new GroupAgentStub());
//        FXMLLoader loader = new FXMLLoader(RightsManagmentController.loadFxml());
//        loader.load();
//        assertThat((RightsManagmentController)loader.getController()).isNotNull();
//    }

}
