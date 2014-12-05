/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.report.action;

import java.awt.event.ActionEvent;

import eu.ggnet.dwoss.report.*;
import eu.ggnet.dwoss.report.ReportController.In;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.authorisation.AccessableAction;

import static eu.ggnet.dwoss.rights.api.AtomicRight.*;
import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class CreateReportAction extends AccessableAction {

    public CreateReportAction() {
        super(CREATE_SALES_REPORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.choiceSwing(CreateViewCask.class)
                .onOk(r -> new In(lookup(ReportAgent.class).prepareReport(r.getParameter(), r.loadUnreported()), false))
                .openFxml(ReportController.class)
                .exec();
        /*
         final CreateViewCask selector = new CreateViewCask();
         OkCancelDialog<CreateViewCask> dialog = new OkCancelDialog<>(lookup(Workspace.class).getMainFrame(), "Report Erstellen", selector);
         dialog.setVisible(true);
         if ( dialog.isCancel() ) return;
         Platform.runLater(() -> {
         try {
         FXMLLoader loader = new FXMLLoader(ReportController.loadFxml());
         AnchorPane root = (AnchorPane)loader.load();
         ReportController controller = Objects.requireNonNull(loader.getController());
         Stage stage = new Stage();
         stage.setTitle("Report Managment");
         stage.setScene(new Scene(root, Color.ALICEBLUE));
         controller.initReportData(lookup(ReportAgent.class).prepareReport(selector.getParameter(), selector.loadUnreported()), false);
         stage.show();
         } catch (IOException ex) {
         DwOssCore.show(dialog, ex);
         }
         });
         */
    }

}
