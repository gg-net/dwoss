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
package tryout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;

import eu.ggnet.dwoss.assembly.client.support.exception.DetailView;
import eu.ggnet.dwoss.assembly.client.support.exception.DwFinalExceptionConsumer;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ui.Frame;

/**
 *
 * @author mirko.schulze
 */
public class TryoutDetailView {

    @Frame
    public static class FramePanel extends JPanel {

        public FramePanel() {
            JButton e = new JButton("Exception");
            e.addActionListener(ex -> {
                CompletableFuture.failedFuture(new IOException("Eine Exception")).handle(UiCore.global().handler(e));
            });
            add(new JLabel("Obendrüber"));
            add(e);

        }

    }

    public static void main(String[] args) {
        JPanel p = new JPanel();
        JButton b1 = new JButton("Show DetailView");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Ui.build().parent(p).title("Systemfehler").swing()
                        .show(() -> new DetailView(message(), longMessage(), veryLongMessage(), "email@example.com"));
            }
        });
        p.add(b1);

        JButton b2 = new JButton("Exception over Parent");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Ui.build(b2).swing().show(FramePanel.class);

            }
        });
        p.add(b2);

        UiCore.continueSwing(UiUtil.startup(() -> p));
        UiCore.global().overwriteFinalExceptionConsumer(new DwFinalExceptionConsumer(() -> "bug@example.com"));

    }

    private static String message() {
        return "Eine Nachricht Nachricht Nachricht Nachricht Nachricht Nachricht Nachricht Nachricht Nachricht Nachricht";
    }

    private static String longMessage() {
        return "Oh Oh"
                + "Oh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh "
                + "OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh OhOh Oh";
    }

    private static String veryLongMessage() {
        return "Oh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh Ja"
                + "Oh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh Ja"
                + "Oh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh JaOh Ja";
    }
}
