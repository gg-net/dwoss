package eu.ggnet.dwoss.misc.op;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.LucidCalcReader;
import eu.ggnet.lucidcalc.SUtil;
import eu.ggnet.lucidcalc.STableColumn;

import java.io.File;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.*;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import eu.ggnet.dwoss.rules.SalesChannel;

import eu.ggnet.dwoss.stock.eao.StockUnitEao;

import lombok.Data;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.*;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.*;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.*;
import static java.awt.Color.*;

/**
 *
 * @author oliver.guenther
 */
@Stateless

public class ImageIdHandlerOperation implements ImageIdHandler {

    @Data
    public final static class ImageIdLine {

        private final String partNo;

        private final String group;

        private final String brand;

        private final String name;

        private final Integer imageId;
    }

    private final static Logger L = LoggerFactory.getLogger(ImageIdHandlerOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Mandator mandator;

    @Override
    public void importMissing(FileJacket inFile) throws UserInfoException {
        final SubMonitor m = monitorFactory.newSubMonitor("Image Ids importieren", 100);
        m.message("Reading File");
        m.start();
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class);
        reader.addColumn(1, String.class);
        reader.addColumn(2, String.class);
        reader.addColumn(3, String.class);
        reader.addColumn(4, Integer.class);
        reader.setHeadline(true);
        reader.setTrim(true);
        File f = inFile.toTemporaryFile();
        List<ImageIdLine> lines = reader.read(f, ImageIdLine.class);
        List<String> errors = reader.getErrors();
        ProductEao productEao = new ProductEao(uuEm);
        m.message("Importing Data");
        m.setWorkRemaining(lines.size());
        for (ImageIdLine line : lines) {
            m.worked(1, "importing " + line.getPartNo());
            if ( line.getImageId() == null ) {
                errors.add("No ImageId for " + line.getPartNo());
                continue;
            }
            Product p = productEao.findByPartNo(line.getPartNo());
            if ( p != null ) {
                p.setImageId(line.getImageId());
            } else {
                errors.add("No Product for '" + line.getPartNo() + "'");
            }
        }
        m.finish();
        if ( !errors.isEmpty() ) throw new UserInfoException(errors);
    }

    /**
     * Returns a FileJacket of XLS, which contains all Products with missing ImageIds.
     * <p/>
     * Acitve Filters are:
     * <ul>
     * <li>If SalesChannel is supplied, only Products, which have units in the SalesChannel.</li>
     * <li>Only Products, which don't have an image id.</li>
     * <li>Only Products, which have Unit in stock.</li>
     * </ul>
     * <p/>
     * @param salesChannel
     * @return
     */
    @Override
    public FileJacket exportMissing(SalesChannel salesChannel) {
        SubMonitor m = monitorFactory.newSubMonitor("Fehlende Image Ids exportieren", 100);
        m.message("Loading Missing IDs");
        m.start();

        List<Integer> uniqueUnitIds = new StockUnitEao(stockEm).findByNoLogicTransactionAsUniqueUnitId();
        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uuEm).findByIds(uniqueUnitIds);

        SortedSet<Product> products = new TreeSet<>();
        for (UniqueUnit uniqueUnit : uniqueUnits) {
            if ( uniqueUnit.getProduct() == null ) continue;
            if ( salesChannel != null && uniqueUnit.getSalesChannel() != salesChannel ) continue;
            if ( uniqueUnit.getProduct().getImageId() > 0 ) continue;
            products.add(uniqueUnit.getProduct());
        }

        STable table = new STable();
        table.setHeadlineFormat(new CFormat(BOLD, BLACK, LIGHT_GRAY, CENTER, MIDDLE));
        table.add(new STableColumn("ArtikelNr", 15).setAction(SUtil.getBeanProperty("partNo")));
        table.add(new STableColumn("Warengruppe", 22).setAction(SUtil.getBeanProperty("group.name")));
        table.add(new STableColumn("Hersteller", 16).setAction(SUtil.getBeanProperty("tradeName.name")));
        table.add(new STableColumn("Bezeichnung", 45).setAction(SUtil.getBeanProperty("name")));
        table.add(new STableColumn("Image Id", 10).setAction(SUtil.getNull()));
        table.setModel(new STableModelList(products));

        CCalcDocument doc = new TempCalcDocument("ImageIds");
        doc.add(new CSheet("ImageIds", table));
        FileJacket fj = new FileJacket("MissingImageIds", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(doc));
        m.finish();
        return fj;
    }
}
