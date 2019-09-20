package tryout;

import java.util.*;

import javax.persistence.LockModeType;
import javax.swing.JLabel;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.report.ee.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ee.*;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.Storeable;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.report.ui.RawReportView;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.core.*;

/**
 *
 * @author oliver.guenther
 */
public class SimpleReportLineTryout {
    
    public static SimpleReportLine makeSimpleReportLine(double amount, TradeName contractor,double contractorReferencePrice, DocumentType documentType, PositionType positionType, double price, String productName,
    double purchasePrice, String refurbishId, String partNo, long uniqueUnitId, Date reportingDate) {
        SimpleReportLine l = new SimpleReportLine();
        l.setAmount(amount);
        l.setContractor(contractor);
        l.setContractorReferencePrice(contractorReferencePrice);
        l.setDocumentType(documentType);
        l.setPositionType(positionType);
        l.setPrice(price);
        l.setProductName(productName);
        l.setPurchasePrice(purchasePrice);
        l.setRefurbishId(refurbishId);
        l.setPartNo(partNo);
        l.setUniqueUnitId(uniqueUnitId);
        l.setReportingDate(reportingDate);
        return l;
    }

    public static void main(String[] args) {
        ReportAgent rastub = new ReportAgent() {
            List<eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine> all
                    = Arrays.asList(makeSimpleReportLine(1,TradeName.ACER,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.DELL,100,DocumentType.INVOICE,PositionType.UNIT,10,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.HP,100,DocumentType.INVOICE,PositionType.UNIT,23,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.AMAZON,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.EMACHINES,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.FUJITSU,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.ONESELF,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.LENOVO,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.ACER,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.FUJITSU,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.SAMSUNG,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.SAMSUNG,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date()),
                            makeSimpleReportLine(1,TradeName.ALSO,100,DocumentType.INVOICE,PositionType.UNIT,50,"ABCDEFG",40,"1234567","AA.BBBBB.CC",1000,new Date())
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
                srl.load(new SearchParameter(""));
                return srl;
            });
        });
    }

}
