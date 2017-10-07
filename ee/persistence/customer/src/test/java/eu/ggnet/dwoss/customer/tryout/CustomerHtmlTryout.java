/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.tryout;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import org.junit.Test;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.rules.Css;

/**
 *
 * @author oliver.guenther
 */
public class CustomerHtmlTryout {

    @Test
    public void tryoutCustomer() throws Exception {
        CustomerGenerator gen = new CustomerGenerator();
        Customer c1 = gen.makeOldCustomer();

        Customer c2 = gen.makeCustomer();

        CountDownLatch latch = new CountDownLatch(1);

        EventQueue.invokeAndWait(() -> {
            final JFXPanel jfxPanel = new JFXPanel();
            Platform.runLater(() -> {
                WebView view = new WebView();
                view.getEngine().loadContent(Css.toHtml5WithStyle(c1.toHtml() + "<hr />" + c2.toHtml()));
                BorderPane p = new BorderPane(view);
                Scene sc = new Scene(p);
                jfxPanel.setScene(sc);
            });
            JFrame f = new JFrame("Tryout");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(jfxPanel);
            f.setSize(500, 900);
            f.setLocation(100, 100);
            f.setVisible(true);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    latch.countDown();
                }

            });

        });

        latch.await();
    }
}
