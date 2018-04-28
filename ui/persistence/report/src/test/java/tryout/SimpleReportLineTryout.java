package tryout;

import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.api.values.DocumentType;

import java.util.*;

import javax.persistence.LockModeType;
import javax.swing.JLabel;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ee.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ee.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.report.ui.RawReportView;
import eu.ggnet.saft.*;
import eu.ggnet.saft.api.Reply;

/**
 *
 * @author oliver.guenther
 */
public class SimpleReportLineTryout {

    public static void main(String[] args) {
        ReportAgent rastub = new ReportAgent() {

            List<eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine> all
                    = Arrays.asList(eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.ACER).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                            .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                            .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.DELL).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(10).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.HP).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(23).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.AMAZON).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.EMACHINES).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.FUJITSU).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.ONESELF).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.LENOVO).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567").partNo("AA.BBBBB.CC").uniqueUnitId(1000)
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.FUJITSU).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567")
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.SAMSUNG).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567")
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.SAMSUNG).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567")
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.AMAZON).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567")
                                    .reportingDate(new Date()).build(),
                            eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.builder().amount(1).contractor(TradeName.ALSO).contractorReferencePrice(100).documentType(DocumentType.INVOICE)
                                    .positionType(PositionType.UNIT).price(50).productName("ABCDEFG").purchasePrice(40).refurbishId("1234567")
                                    .reportingDate(new Date()).build()
                    );

            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                if ( entityClass.equals(eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.class) ) return (List<T>)all;
                return Collections.EMPTY_LIST;
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
                if ( entityClass.equals(eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.class) ) {
                    if ( start > all.size() ) return Collections.EMPTY_LIST;
                    if ( start < 0 ) start = 0;
                    if ( amount + start > all.size() ) amount = all.size() - start;
                    return (List<T>)all.subList(start, amount + start);
                }
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine> findSimple(SearchParameter search, int firstResult, int maxResults) {
                return findAll(eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.class, firstResult, maxResults);
            }

            @Override
            public long count(SearchParameter search) {
                return all.size();
            }

            @Override
            public <T> long count(Class<T> entityClass) {
                if ( entityClass.equals(eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine.class) ) return all.size();
                return 0;
            }

            //<editor-fold defaultstate="collapsed" desc="Unused Methods">
            @Override
            public Report store(Report report, Collection<Storeable> storeables) {
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
            public List<ReportLine> find(SearchParameter search, int firstResult, int maxResults) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ViewReportResult findReportResult(long reportId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<ReportLine> findReportLinesByDocumentType(DocumentType type, Date from, Date till) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ViewReportResult prepareReport(ReportParameter p, boolean loadUnreported) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean updateReportLineComment(int optLock, long reportId, String comment) {
                System.out.println("Line = " + reportId + " adding comment " + comment);
                return true;
            }

            @Override
            public Reply<String> updateReportName(Report.OptimisticKey key, String name) {
                System.out.println("Report = " + key + " changing name = " + name);
                return Reply.success(name);
            }
            //</editor-fold>

        };
        Dl.remote().add(ReportAgent.class, rastub);
        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Main Applikation"));

            Ui.build().fx().show(() -> {
                RawReportView srl = new RawReportView();
                srl.load(new SearchParameter());
                return srl;
            });
        });
    }

}
