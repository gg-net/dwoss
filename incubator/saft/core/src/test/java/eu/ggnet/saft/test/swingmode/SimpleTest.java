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
package eu.ggnet.saft.test.swingmode;

import java.awt.GraphicsEnvironment;
import java.awt.Point;

import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Assume;
import org.junit.Test;

import eu.ggnet.saft.UiCore;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing the start of the Ui opening a dialog and thats it.
 *
 * @author oliver.guenther
 */
public class SimpleTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        Assume.assumeFalse("No Grahics Environment", GraphicsEnvironment.isHeadless());
        UiCore.startSwing(() -> new MainPanel());
        window = WindowFinder.findFrame("main").using(robot());
    }

    @Test
    public void test() {
        robot().waitForIdle();
        window.button("showJPanel").click();
        DialogFixture dialog = window.dialog("A JPanel");
        assertThat(dialog).isNotNull();
        dialog.label().requireText("A Text");

        dialog.moveTo(new Point(10, 10)); // Move it away, as it blocks the click

        window.button("showPane").click();

        dialog = window.dialog("pane");
        assertThat(dialog).isNotNull();
        window.close();
        robot().waitForIdle();
    }

}
