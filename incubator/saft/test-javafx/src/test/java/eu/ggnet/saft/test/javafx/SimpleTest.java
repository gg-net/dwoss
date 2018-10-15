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
package eu.ggnet.saft.test.javafx;

import javafx.scene.control.Labeled;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import eu.ggnet.saft.core.UiCore;

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

    @Test
    @Ignore // TODO: UI Tests seam to fail on different Screne sizes or OSs.
    public void test() throws InterruptedException {
        Thread.sleep(500);
        clickOn("#showPane");
        Thread.sleep(500);                        

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
