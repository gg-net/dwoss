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
package eu.ggnet.dwoss.util;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

/**
 *
 * @author oliver.guenther
 */
public class HtmlPane extends BorderPane implements Consumer<String> {

    private WebView webView;

    public HtmlPane() {
        System.out.println("geht los");
        CountDownLatch cdl = new CountDownLatch(1);
        Platform.runLater(() -> {
            System.out.println("geht weiter");
            webView = new WebView();
            setCenter(webView);
            cdl.countDown();
            System.out.println("geht gut");
        });
        try {
            cdl.await();
            System.out.println("geht mehr");
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void accept(String content) {
        webView.getEngine().loadContent(content);
    }

}
