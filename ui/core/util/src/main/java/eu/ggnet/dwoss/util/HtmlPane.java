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

import java.util.concurrent.*;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Html Pane for Strings.
 * <p>
 * @a
 * import static java.util.concurrent.TimeUnit.SECONDS;
 * uthor oliver.guenther
 */
public class HtmlPane extends BorderPane implements Consumer<String> {

    private WebView webView;

    public HtmlPane() {
        dispatch(() -> {
            setPadding(new Insets(5));
            webView = new WebView();
            setCenter(webView);
            return null;
        });
    }

    @Override
    public void accept(String content) {
        dispatch(() -> {
            webView.getEngine().loadContent(content);
            return null;
        });
    }

    public static <T> T dispatch(Callable<T> callable) throws RuntimeException {
        try {
            FutureTask<T> futureTask = new FutureTask<>(callable);
            final CountDownLatch cdl = new CountDownLatch(1);
            if ( Platform.isFxApplicationThread() ) {
                futureTask.run();
                cdl.countDown();
            } else {
                Platform.runLater(() -> {
                    futureTask.run();
                    cdl.countDown();
                });
            }
            cdl.await(5, SECONDS);
            return futureTask.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
