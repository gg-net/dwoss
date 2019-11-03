package tryout.neo;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ui.neo.CommunicationUpdateController;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;


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
/**
 *
 * @author jens.papenhagen
 */
public class CommunicationUpdateTryout {

    public static void main(String[] args) {
        CustomerGenerator gen = new CustomerGenerator();
        Communication comm = gen.makeCommunicationWithId(1111);         

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton editButton = new JButton("Edit");

        editButton.addActionListener(ev -> {
            if ( comm.getViolationMessage() != null ) {
                System.out.println("Communication ViolationMessages: " + comm.getViolationMessage());
            }

            Ui.exec(() -> {
                Ui.build().fxml().eval(() -> comm, CommunicationUpdateController.class).opt().ifPresent(System.out::println);
            });
        });

        JButton addButton = new JButton("Add");

        addButton.addActionListener(ev -> {
            Ui.exec(() -> {
                Ui.build().fxml().eval(CommunicationUpdateController.class).opt().ifPresent(System.out::println);
            });
        });

        JPanel p = new JPanel();
        p.add(editButton);
        p.add(addButton);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
