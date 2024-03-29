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

import java.util.*;

import jakarta.persistence.LockModeType;
import javax.swing.JLabel;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.report.ee.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ee.*;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.report.ui.cap.support.SelectExistingReportView;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 *
 * @author jens.papenhagen
 */
public class SelectExistingReportViewTryout {

    public static void main(String[] args) {
        ReportAgent rastub = new ReportAgent() {

            private int counter = 0;

            //<editor-fold defaultstate="collapsed" desc="Unused Methods">
            @Override
            public List<SimpleReportLine> findSimple(SearchParameter search, int firstResult, int maxResults) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<ReportLine> find(SearchParameter search, int firstResult, int maxResults) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public long count(SearchParameter search) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ViewReportResult prepareReport(ReportParameter p, boolean loadUnreported) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<ReportLine> findReportLinesByDocumentType(DocumentType type, Date from, Date till) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Report store(Report report, Collection<Storeable> storeables) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ViewReportResult findReportResult(long reportId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<ReportLine> findAllReportLinesReverse(int firstResult, int maxResults) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<ReportLine> attachDanglingComplaints(TradeName type, Date till) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> long count(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean updateReportLineComment(int optLock, long reportId, String comment) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            //</editor-fold>
            @Override
            public String updateReportName(Report.OptimisticKey key, String name) {
                System.out.println("Report Name updated with " + name + " calles");
                counter++;
                if ( (counter % 2) != 0 ) {
                    System.out.println("Counter=" + counter + " simulating Error");
                    throw new IllegalArgumentException("Counter=" + counter + " simulating Error");
                } else {
                    System.out.println("Counter=" + counter + " simulating Success");
                    return name;
                }
            }

        };
        Dl.remote().add(ReportAgent.class, rastub);

        UiCore.startSwing(() -> new JLabel("Main Applikation"));

        final Date today = new Date();
        final Date enddate = new Date();
        final Date startdate = new Date();

        final Set<TradeName> trades = new HashSet<>();
        trades.add(TradeName.HP);
        trades.add(TradeName.ACER);

        //build some sample Reports
        List<Report> allReports = new ArrayList();
        for (int i = 0; i < 8; i++) {
            for (TradeName traden : trades) {
                startdate.setYear(62 + i);
                long sum = today.getTime() + startdate.getTime();
                Date sumDate = new Date(sum);

                String name = "Report: " + i + "-" + traden + "-" + sumDate.toString();

                Report e = new Report(name, traden, sumDate, enddate);
                allReports.add(e);
            }
        }
        Ui.exec(() -> {
            Ui.build().fx().show(() -> allReports, () -> new SelectExistingReportView());
        });
    }
}
