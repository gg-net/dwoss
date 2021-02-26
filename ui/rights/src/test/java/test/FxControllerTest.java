/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.junit.Test;

import eu.ggnet.dwoss.rights.ui.*;
import eu.ggnet.saft.core.ui.FxController;

import static org.assertj.core.api.Assertions.assertThat;

public class FxControllerTest {

    @Test
    public void controllerValid() {
        assertThat(FxController.validationMessage(RightsManagementController.class)).isNull();
        assertThat(FxController.validationMessage(UserManagementController.class)).isNull();
        assertThat(FxController.validationMessage(GroupManagementController.class)).isNull();
    }
}