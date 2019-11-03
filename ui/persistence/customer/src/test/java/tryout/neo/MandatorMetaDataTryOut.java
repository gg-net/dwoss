package tryout.neo;

import javax.swing.JButton;
import javax.swing.JPanel;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ui.neo.MandatorMetaDataController;
import eu.ggnet.dwoss.mandator.api.Mandators;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.dl.RemoteLookup;
import eu.ggnet.saft.experimental.auth.Guardian;

import tryout.stub.GuardianStub;
import tryout.stub.MandatorsStub;

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
 * @author jacob.weinhold
 */
public class MandatorMetaDataTryOut {

    public static void main(String[] args) {

        Dl.local().add(RemoteLookup.class, new RemoteLookup() {
            @Override
            public <T> boolean contains(Class<T> clazz) {
                return false;
            }

            @Override
            public <T> T lookup(Class<T> clazz) {
                return null;
            }
        });
        Dl.remote().add(Mandators.class, new MandatorsStub());
        Dl.local().add(Guardian.class, new GuardianStub());

        JButton close = new JButton("Schliessen");
        close.addActionListener(e -> Ui.closeWindowOf(close));

        JButton run = new JButton("OpenUi");
        run.addActionListener(ev -> {

            CustomerGenerator gen = new CustomerGenerator();
            MandatorMetadata mData = gen.makeMandatorMetadata();
            System.out.println("Edit: " + mData);
            

            Ui.build().title("Mandant Demo").fxml().eval(() -> mData, MandatorMetaDataController.class).cf().thenAccept(System.out::println).handle(Ui.handler());

        });

        JPanel p = new JPanel();
        p.add(run);
        p.add(close);

        UiCore.startSwing(() -> p);
    }

}
