package eu.ggnet.dwoss.misc.op.movement;

import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.stock.entity.Stock;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import eu.ggnet.dwoss.common.log.AutoLogger;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;

/**
 *
 * @author oliver.guenther
 */
@Stateless
@AutoLogger
public class MovementListingProducerOperation implements MovementListingProducer {

    private final static Logger L = LoggerFactory.getLogger(MovementListingProducerOperation.class);

    @Inject
    private CustomerService customerService;

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
        SubMonitor m = monitorFactory.newSubMonitor("Versand und Abholung", 100);
        m.message("lade Vorgänge");
        m.start();
        LogicTransactionEao ltEao = new LogicTransactionEao(stockEm);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        List<Document> documents = new DocumentEao(redTapeEm).findActiveByDirective(listType.getDirective());
        m.worked(5);
        m.setWorkRemaining(documents.size() + 2);
        List<MovementLine> lines = new ArrayList<>();
        List<String> dossierids = new ArrayList<>();
        for (Document document : documents) {
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
        String title = listType.getName() + " - " + stock.getName();
        L.info("generateList {} containing {}", title, dossierids);
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

    private boolean hasUnitOnStock(LogicTransaction lt, Stock stock) {
        if ( lt == null || lt.getUnits() == null ) return false;
        for (StockUnit stockUnit : lt.getUnits()) {
            if ( Objects.equals(stockUnit.getStock(), stock) ) return true;
        }
        return false;
    }
}
