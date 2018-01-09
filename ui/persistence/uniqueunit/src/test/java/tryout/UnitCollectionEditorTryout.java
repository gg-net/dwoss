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
package tryout;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ui.product.UnitCollectionEditorController;
import eu.ggnet.saft.*;

import tryout.stub.UniqueUnitAgentStub;

/**
 *
 * @author jens.papenhagen
 */
public class UnitCollectionEditorTryout {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        Client.addSampleStub(UniqueUnitAgent.class, new UniqueUnitAgentStub());

//        JButton close = new JButton("Schliessen");
//        close.addActionListener(e -> Ui.closeWindowOf(close));
//
//        JButton run = new JButton("Öffne UI");
//        run.addActionListener(ev -> {
//            Ui.fxml().show(UnitCollectionEditorController.class);
//
//        });
//
//        JPanel p = new JPanel();
//        p.add(run);
//        p.add(close);

        Ui.fxml().show(UnitCollectionEditorController.class);
    }

}
