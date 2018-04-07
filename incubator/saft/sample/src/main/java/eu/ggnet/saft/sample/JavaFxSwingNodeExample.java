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
package eu.ggnet.saft.sample;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import eu.ggnet.saft.core.ui.SwingSaft;

import lombok.Value;

/**
 *
 * @author oliver.guenther
 */
public class JavaFxSwingNodeExample extends Application {

    @Value
    public static class C {

        private final JButton button;

        private final JPanel panel;
    }

    final Map<Component, SwingNode> JAVAFX_PARENT_HELPER = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane bp = new BorderPane();
        bp.setTop(new Label("Oben"));
        SwingNode snq = new SwingNode();

        C c = SwingSaft.dispatch(() -> {

            JPanel p1 = new JPanel(new BorderLayout());
            JButton b1 = new JButton("Der Siwng Knopf");
            b1.addActionListener(System.out::println);
            p1.add(b1);
            snq.setContent(p1);
            JAVAFX_PARENT_HELPER.put(p1, snq);

            System.out.println("in:" + p1.getPreferredSize());

            return new C(b1, p1);
        });
        Dimension preferredSize = c.panel.getPreferredSize();
        System.out.println(preferredSize);


        /*
        . iterate down the swing parents until empty.
        then back and get the last JPanel ( not the lightwight....)
        use that as key for the

         */
        BorderPane wrap = new BorderPane(snq);
        wrap.setBottom(new Label("Blaaaaa"));
        wrap.setPrefHeight(preferredSize.getHeight());  // Size to JavaFx
        wrap.setPrefWidth(preferredSize.getWidth());
        bp.setCenter(wrap);

        primaryStage.setScene(new Scene(bp));
        primaryStage.sizeToScene();
        primaryStage.show();

        System.out.println(find(c.button));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public SwingNode find(Component c) {
        if ( c == null ) throw new IllegalArgumentException("Supplied Componente was not in the helper tree");
        if ( JAVAFX_PARENT_HELPER.containsKey(c) ) return JAVAFX_PARENT_HELPER.get(c);
        return find(c.getParent());
    }

    public static void next(Component c) {
        if ( c == null ) return;
        System.out.println(c);
        next(c.getParent());
    }

}
