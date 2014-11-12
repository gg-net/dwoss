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
package eu.ggnet.dwoss.misc;

import eu.ggnet.dwoss.util.MetawidgetConfig;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.dwoss.common.ExceptionUtil;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import javax.validation.constraints.NotNull;

import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.annotation.UiLabel;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.swing.widgetprocessor.binding.beansbinding.BeansBindingProcessor;

import eu.ggnet.saft.core.Workspace;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.uniqueunit.op.UniqueUnitReporter;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.validation.ValidationUtil;

import lombok.Data;

import static eu.ggnet.saft.core.Client.lookup;

/**
 *
 * @author pascal.perau
 */
public class UnitQualityReportAction extends AbstractAction {

    @Data
    public static class ReportParameter {

        @NotNull
        private Date start;

        @UiComesAfter("start")
        @NotNull
        private Date end;

        @NotNull
        @UiLabel("Lieferant")
        private TradeName contractor = TradeName.values()[0];
    }

    public UnitQualityReportAction() {
        super("Gerätequalitätsreport");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingMetawidget mw = MetawidgetConfig.newSwingMetaWidget(TradeName.class);
        mw.setToInspect(new ReportParameter());
        OkCancelDialog dialog = new OkCancelDialog(lookup(Workspace.class).getMainFrame(), "Report über Gerätequalität", mw);
        dialog.setVisible(true);
        if ( !dialog.isOk() ) return;
        mw.getWidgetProcessor(BeansBindingProcessor.class).save(mw);
        final ReportParameter rp = mw.getToInspect();
        if ( !ValidationUtil.isValidOrShow(lookup(Workspace.class).getMainFrame(), rp) ) return;

        new SwingWorker<FileJacket, Object>() {
            @Override
            protected FileJacket doInBackground() throws Exception {
                return lookup(UniqueUnitReporter.class).quality(rp.getStart(), rp.getEnd(), rp.getContractor());
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(get().toTemporaryFile());
                } catch (InterruptedException | ExecutionException | IOException ex) {
                    ExceptionUtil.show(lookup(Workspace.class).getMainFrame(), ex);
                }
            }
        }.execute();
    }
}
