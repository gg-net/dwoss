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
package eu.ggnet.dwoss.redtapext.ui.cao.jasper;

import java.awt.EventQueue;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrintManager;

import eu.ggnet.dwoss.core.jasper.AbstractJasperFxView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author oliver.guenther
 */
public class DocumentJasperFxView extends AbstractJasperFxView implements Consumer<DocumentJasper>, ResultProducer<DocumentJasperResult> {

    private final Button btnEmail;

    private DocumentJasper in;

    private boolean correctlyBriefed = false;

    public DocumentJasperFxView() {
        super();
        btnEmail = new Button(null, new ImageView(getClass().getResource("mail.png").toExternalForm()));
        btnEmail.setPrefSize(30, 30);
        btnEmail.setOnAction(e -> mail());
        btnEmail.setDisable(true);
        menu.getChildren().add(2, btnEmail);

    }

    @Override
    protected void print() {
        EventQueue.invokeLater(() -> {
            try {
                JasperPrintManager.printReport(in.jasperPrint(), true);
                correctlyBriefed = true;
            } catch (JRException ex) {
                UiCore.global().handle(btnEmail, ex);
            }
        });
    }

    @Override
    public void accept(DocumentJasper in) {
        this.in = in;
        btnEmail.setDisable(in.mailCallback().isEmpty());
        setJasperPrint(in.jasperPrint());
    }

    @Override
    public DocumentJasperResult getResult() {
        return new DocumentJasperResult.Builder().document(in.document()).correctlyBriefed(correctlyBriefed).build();
    }

    private void mail() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(100, 100);
        getChildren().add(pi);
        setDisabled(true);

        Ui.exec(() -> {
            try {
                in.mailCallback().get().run();
                correctlyBriefed = true;
                Ui.build(this).alert("Dokument per Mail versendet");
            } catch (Exception ex) {
                UiCore.global().handle(btnEmail, ex);
            } finally {
                Platform.runLater(() -> {
                    getChildren().remove(pi);
                    setDisabled(false);
                });
            }
        });
    }
}
