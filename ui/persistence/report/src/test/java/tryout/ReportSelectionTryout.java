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

import javax.persistence.LockModeType;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.junit.Test;

import eu.ggnet.dwoss.report.ReportAgent;
import eu.ggnet.dwoss.report.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ReportSelectionPane;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.OkCancelStage;
import eu.ggnet.saft.core.Client;

/**
 *
 * @author jens.papenhagen
 */
public class ReportSelectionTryout {

    private boolean complete = false;

    @Test
    public void tryout() throws InterruptedException {
        ReportAgent rastub = new ReportAgent() {
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
            public boolean updateReportName(int optLock, long reportId, String name) {
                System.out.println("Report Name get updated");
                return true;
            }


        };
        Client.addSampleStub(ReportAgent.class, rastub);
        JFXPanel jfxPanel = new JFXPanel();

        Date today = new Date();
        Date enddate = new Date();
        Date startdate = new Date();

        Set<TradeName> trades = new HashSet<>();
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

        Platform.runLater(() -> {
            ReportSelectionPane selector = new ReportSelectionPane();
            selector.accept(allReports);

            OkCancelStage<ReportSelectionPane> stage = new OkCancelStage<>("Auswählen", selector);
            stage.showAndWait();
            System.out.println("OK=" + stage.isOk());
            System.out.println(selector);
            complete = true;
        });

        while (!complete) {
            Thread.sleep(500);
        }

    }
}
