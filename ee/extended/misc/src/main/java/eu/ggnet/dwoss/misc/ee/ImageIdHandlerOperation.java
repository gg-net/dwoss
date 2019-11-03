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
package eu.ggnet.dwoss.misc.ee;

import java.io.File;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;
import eu.ggnet.saft.api.Reply;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.MIDDLE;
import static java.awt.Color.BLACK;
import static java.awt.Color.LIGHT_GRAY;

/**
 *
 * @author oliver.guenther
 */
@Stateless

public class ImageIdHandlerOperation implements ImageIdHandler {

    public final static class ImageIdLine {

        private final String partNo;

        private final String group;

        private final String brand;

        private final String name;

        private final Integer imageId;

        public ImageIdLine(String partNo, String group, String brand, String name, Integer imageId) {
            this.partNo = partNo;
            this.group = group;
            this.brand = brand;
            this.name = name;
            this.imageId = imageId;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
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

    @Override
    public Reply<Void> importMissing(FileJacket inFile) {
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
            m.worked(1, "importing " + line.partNo);
            if ( line.imageId == null ) {
                errors.add("No ImageId for " + line.partNo);
                continue;
            }
            Product p = productEao.findByPartNo(line.partNo);
            if ( p != null ) {
                p.setImageId(line.imageId);
            } else {
                errors.add("No Product for '" + line.imageId + "'");
            }
        }
        m.finish();
        if ( !errors.isEmpty() ) return Reply.failure(errors.toString());
        return Reply.success(null);
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
