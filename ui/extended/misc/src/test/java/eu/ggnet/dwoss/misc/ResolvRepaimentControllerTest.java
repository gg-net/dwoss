/*
 * Copyright (C) 2014 bastian.venz
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

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.*;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;

import org.junit.Test;

import eu.ggnet.dwoss.misc.op.ResolveRepayment;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.core.Client;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author bastian.venz
 */
public class ResolvRepaimentControllerTest {

    @Test
    public void testResource() {
        assertThat(ResolveRepaymentController.loadFxml()).isNotNull();
    }

    @Test
    public void testJavaFxFxml() throws IOException {
        if ( GraphicsEnvironment.isHeadless() ) return;
        Client.addSampleStub(ResolveRepayment.class, new ResolveRepayment() {

            @Override
            public List<ReportLine> getRepaymentLines(TradeName contractor) {
                return Collections.EMPTY_LIST;
            }

            @Override
            public void resolveUnit(String identifier, TradeName contractor, String arranger, String comment) throws UserInfoException {
            }
        });
        new JFXPanel(); // Implizit start of JavaFx.
        FXMLLoader loader = new FXMLLoader(ResolveRepaymentController.loadFxml());
        loader.load();
        assertThat((ResolveRepaymentController)loader.getController()).isNotNull();
    }

}
