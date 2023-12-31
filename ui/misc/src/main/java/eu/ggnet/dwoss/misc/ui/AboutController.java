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
package eu.ggnet.dwoss.misc.ui;

import java.util.Objects;
import java.util.function.Consumer;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import eu.ggnet.dwoss.misc.ui.AboutController.In;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.Title;

import jakarta.enterprise.context.Dependent;

/**
 *
 * @author oliver.guenther
 */
@Dependent
@Title("Über ...")
public class AboutController implements FxController, Consumer<In> {

    public static class In {

        public final String info;

        public final String debug;

        public In(String info, String debug) {
            this.info = Objects.requireNonNull(info, "info must not be null");
            this.debug = Objects.requireNonNull(debug, "debug must not be null");
        }

    }

    @FXML
    private Button closeButton;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private TextArea debugTextArea;

    // Todo: This should be removed here an pushed via in. https://jira.cybertron.global/browse/DWOSS-335
    @Inject
    private BeanManager beanManager;

    @FXML
    public void initialize() {
        closeButton.setOnAction((e) -> Ui.closeWindowOf(closeButton));
    }

    @Override
    public void accept(In in) {
        Objects.requireNonNull(in, "in must not be null");
        infoTextArea.setText(in.info + "\n - CDI enabled: " + (beanManager == null ? "No" : "Yes"));
        debugTextArea.setText(in.debug);
    }

}
