/*
 * Copyright (C) 2019 GG-Net GmbH
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

import javafx.application.Application;
import javafx.stage.Stage;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.sample.impl.Sample;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapeSamples;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporterOperation;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.JasperFxView;
import eu.ggnet.dwoss.redtapext.ui.cao.jasper.JasperFxViewData;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class JasperFxViewTryout extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        UiCore.startJavaFx(primaryStage, () -> {

            DocumentSupporterOperation documentSupporter = new DocumentSupporterOperation();
            documentSupporter.setMandator(Sample.MANDATOR);;
            JasperFxView jp = new JasperFxView();
            Document doc = RedTapeSamples.getDocument();
            try {
            JasperFxViewData in = new JasperFxViewData.Builder()
                    .document(doc)
                    .jasperPrint(documentSupporter.render(doc, DocumentViewType.DEFAULT))
                    .mailCallback(() -> {
                        try {
                            System.out.println("Starting mail send");
                            Thread.sleep(2000);
                            System.out.println("Ending mail send");
                        } catch (InterruptedException ex) {
                        }
                    }).build();
            jp.accept(in);
            } catch (UserInfoException ex) {
                throw new RuntimeException(ex);
            }
            return jp;
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
