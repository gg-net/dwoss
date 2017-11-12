/*
 * Copyright (C) 2014 GG-Net GmbH
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
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;

import org.junit.Test;

import eu.ggnet.dwoss.util.HtmlPane;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class HtmlPaneTryout {

    @Test
    public void tryout() throws InterruptedException {
        JButton b = new JButton("Press to close");
        b.setPreferredSize(new Dimension(200, 50));
        CountDownLatch l = new CountDownLatch(1);
        b.addActionListener(e -> {
            l.countDown();
        });
        UiCore.startSwing(() -> b);

        Ui.call(() -> "<b>Bold Text</b>")
                .openFx(HtmlPane.class)
                .exec();

        l.await();
    }

}
