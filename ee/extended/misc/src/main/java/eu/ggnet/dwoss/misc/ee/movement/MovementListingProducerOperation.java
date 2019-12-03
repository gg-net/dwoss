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
package eu.ggnet.dwoss.misc.ee.movement;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.lucidcalc.*;

import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.MIDDLE;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.TOP;

/**
 *
 * @author oliver.guenther
 */
@Stateless
@AutoLogger
public class MovementListingProducerOperation implements MovementListingProducer {

    private final static Logger L = LoggerFactory.getLogger(MovementListingProducerOperation.class);

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Override
    public JasperPrint generateList(ListType listType, Stock stock) {
        List<MovementLine> lines = generateLines(listType, stock);
        SubMonitor m = monitorFactory.newSubMonitor("Versand und Abholung - PDF", 10);
        m.start();
        String title = listType.description + " - " + stock.getName();
        m.message("erzeuge Report");
        Map<String, Object> reportParameter = new HashMap<>();
        reportParameter.put("TITLE", title);
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(lines);
        String name = "MovementList.jrxml";
        URL url = Objects.requireNonNull(getClass().getResource(name), "The Resource " + getClass().getPackage() + "/" + name + " not found.");
        try (InputStream is = url.openStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(is);
            JasperPrint result = JasperFillManager.fillReport(jasperReport, reportParameter, datasource);
            return result;
        } catch (IOException | JRException e) {
            L.error("Exception during movementList", e);
            throw new RuntimeException(e);
        } finally {
            m.finish();
        }
    }

    @Override
    public FileJacket generateXls(ListType listType, Stock stock) {
        List<MovementLine> lines = generateLines(listType, stock);
        SubMonitor m = monitorFactory.newSubMonitor("Versand und Abholung - XLS", 10);
        m.start();
        String title = listType.description + " - " + stock.getName();
        m.message("erzeuge Report");

        List<Object[]> rows = new ArrayList<>();

        for (MovementLine ml : lines) {
            for (MovementSubline sl : ml.getMovementSublines()) {
                rows.add(new Object[]{
                   sl.getRefurbishId(),
                    sl.getDescription(),
                    ml.getDeliveryAddress().replaceAll("\n", ","),
                    ml.getInvoiceAddress().replaceAll("\n", ",")
                });
            }
        }

        STable unitTable = new STable();

        unitTable.setTableFormat(new CFormat(CENTER, TOP, new CBorder(Color.GRAY, CBorder.LineStyle.THIN), true));
        unitTable.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.LIGHT_GRAY, CENTER, MIDDLE));
        unitTable.setRowHeight(1000);

        unitTable.add(new STableColumn("SopoNr", 12));
        unitTable.add(new STableColumn("Bezeichnung", 30));
        unitTable.add(new STableColumn("Lieferaddresse", 60));
        unitTable.add(new STableColumn("Rechnungsaddresse", 60));

        unitTable.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument();
        cdoc.add(new CSheet("Versand und Abholung", unitTable));
        m.finish();
        return new FileJacket(title + " Liste", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc));
    }

    private boolean hasUnitOnStock(LogicTransaction lt, Stock stock) {
        if ( lt == null || lt.getUnits() == null ) return false;
        for (StockUnit stockUnit : lt.getUnits()) {
            if ( Objects.equals(stockUnit.getStock(), stock) ) return true;
        }
        return false;
    }

    /**
     * Generates all MovementLines of the supplied list type and stock for future presentation.
     *
     * @param listType the list type
     * @param stock    the stock
     * @return all movement lines.
     */
    private List<MovementLine> generateLines(ListType listType, Stock stock) {
        SubMonitor m = monitorFactory.newSubMonitor("Versand und Abholung", 100);
        m.message("lade Vorgänge");
        m.start();
        LogicTransactionEao ltEao = new LogicTransactionEao(stockEm);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        List<Document> documents = new DocumentEao(redTapeEm).findActiveByDirective(listType.directive);
        m.worked(5);
        m.setWorkRemaining(documents.size() + 2);
        List<MovementLine> lines = new ArrayList<>();
        List<String> dossierids = new ArrayList<>();
        List<Long> systemCustomers = customerService.allSystemCustomerIds();
        for (Document document : documents) {
            if ( systemCustomers.contains(document.getDossier().getCustomerId()) ) continue;
            m.worked(1, "verarbeite " + document.getDossier().getIdentifier());
            LogicTransaction lt = ltEao.findByDossierId(document.getDossier().getId());
            if ( !hasUnitOnStock(lt, stock) ) continue; // Filter by stock
            MovementLine line = new MovementLine();
            line.setCustomerId(document.getDossier().getCustomerId());
            line.setCustomerComment(customerService.findComment(document.getDossier().getCustomerId()));
            line.setComment(document.getDossier().getComment());
            line.setInvoiceAddress(document.getInvoiceAddress().getDescription());
            line.setDeliveryAddress(document.getShippingAddress().getDescription());
            line.setDossierIdentifier(document.getDossier().getIdentifier());
            dossierids.add(document.getDossier().getIdentifier());
            line.setPaymentMethod(document.getDossier().getPaymentMethod().getNote());
            for (StockUnit stockUnit : lt.getUnits()) {
                UniqueUnit uniqueUnit = uniqueUnitEao.findById(stockUnit.getUniqueUnitId());
                Product p = uniqueUnit.getProduct();
                MovementSubline elem = new MovementSubline(1,
                        (p == null ? "Kein Produkt" : ProductFormater.toName(p) + " (" + p.getGroup().getNote() + ")"),
                        uniqueUnit.getRefurbishId(),
                        (stockUnit.getStock() == null ? stockUnit.getTransaction().toString() : stockUnit.getStock().getName()),
                        uniqueUnit.getInternalComments().contains(UniqueUnit.StaticInternalComment.PREPARED_SHIPMENT));
                line.addMovementSubline(elem);
            }
            lines.add(line);
        }
        L.info("generateLines({},{}) containing {}", listType, stock, dossierids);
        m.finish();
        return lines;
    }

}
