/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ee.tryout;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import eu.ggnet.dwoss.misc.ee.movement.MovementLine;
import eu.ggnet.dwoss.misc.ee.movement.MovementListJrxml;

/**
 *
 * @author oliver.guenther
 */
public class MovementListTryout {

    public static void main(String[] args) {

        List<MovementLine> lines = MovementLine.makeSamples();
        Map<String, Object> reportParameter = new HashMap<>();
        reportParameter.put("TITLE", "Versandliste");
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(lines);
        try (InputStream is = MovementListJrxml.getResource().openStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(is);
            JasperPrint result = JasperFillManager.fillReport(jasperReport, reportParameter, datasource);
            JRViewer viewer = new JRViewer(result);
            JFrame frame = new JFrame("Viewer");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(viewer, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (IOException | JRException e) {
            e.printStackTrace();
        }
    }
}
