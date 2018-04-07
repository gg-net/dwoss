/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.test.javafxmode;

import javafx.scene.control.Labeled;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import eu.ggnet.saft.UiCore;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class SimpleTest extends ApplicationTest {

    static Logger L = LoggerFactory.getLogger(SimpleTest.class);

    @Override
    public void start(Stage stage) throws Exception {
        L.info("B");
        UiCore.startJavaFx(stage, () -> new MainPane());
        L.info("C");
    }

    /*
        The Test itself works, but in the full clean/build all tests are executed. And it seams, that the swing tests before somehow halt the execution of
        these test. And if surefire is enabled with fork, class not found exeptions ocure.
     */
    // TODO: Fixme, reaktivate
    @Ignore
    @Test
    public void test() {
        clickOn("#showPane");

        // Finding the label in the opened window. If it exists, it implies, that the dialog is visible.
        Labeled label = lookup("#label").queryLabeled();
        assertThat(label).isNotNull().returns("A Text", Labeled::getText);

    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}
