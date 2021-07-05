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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 * An Operation, that takes a List of RefurbisIds and verifies
 *
 * @author oliver.guenther
 */
@Stateless
public class StockTakingOperation implements StockTaking {

    static class ReaderResult {

        public ReaderResult(List<List<?>> refurbisIds, List<String> errors) {
            this.refurbisIds = new ArrayList<>();
            for (List<?> idlist : refurbisIds) {
                for (Object o : idlist) {
                    this.refurbisIds.add(o.toString());
                }
            }
            this.errors = errors;
        }

        public final List<String> refurbisIds;

        public final List<String> errors;
    }

    private static final Logger L = LoggerFactory.getLogger(StockTakingOperation.class);

    @Inject
    private MonitorFactory monitorFactory;

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

    /**
     * Takes the supplied list of refurbishIds, validates their existence in the supplied Stock or all if none supplied.
     *
     * @param inFile  a XLS File containing the refurbishIds in the first sheet, first column.
     * @param stockId the stock, may be null
     * @return a FileJacket with the Result as XLS Report.
     */
    @Override
    public FileJacket fullfillDetails(FileJacket inFile, Integer stockId) {
        SubMonitor m = monitorFactory.newSubMonitor("Inventur vervollständigen", 100);
        m.start();
        m.message("Datei einlesen");
        ReaderResult read = xlsToList(inFile);
        m.worked(3);
        m.setWorkRemaining(read.refurbisIds.size() * 2 + 10);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        DossierEao dossierEao = new DossierEao(redTapeEm);
        List<Object[]> result = new ArrayList<>();
        Set<StockUnit> found = new HashSet<>();
        String stockTaking = "erfasst";
        for (String refurbishId : read.refurbisIds) {
            m.worked(1, "vervollständige " + refurbishId);
            UniqueUnit uu = uniqueUnitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
            StockUnit stu = (uu == null ? null : stockUnitEao.findByUniqueUnitId(uu.getId()));
            if ( stu != null ) found.add(stu);
            if ( uu == null ) {
                result.add(new Object[]{stockTaking, "Fehler: Gerät exitiert nicht !", refurbishId,
                    null, null, null, null, null, null, null, null, null, null, null});
            } else {
                String partNo = uu.getProduct().getPartNo();
                String contractorPartNo = uu.getProduct().getAdditionalPartNo(uu.getContractor());
                String name = ProductFormater.toName(uu.getProduct());
                if ( stu == null ) {
                    result.add(new Object[]{stockTaking, "Nicht im Lager", refurbishId,
                        partNo, uu.getSerial(), name, uu.getContractor(), null, uu.getSalesChannel(), null, null, null, contractorPartNo, null});
                } else {
                    // jetzt schauen was mit st ist
                    String stock = (stu.getStock() == null ? stu.getTransaction().toSimpleLine() : stu.getStock().getName());
                    if ( stu.getLogicTransaction() == null ) {
                        result.add(new Object[]{stockTaking, "verfügbar", refurbishId,
                            partNo, uu.getSerial(), name, uu.getContractor(), stock, uu.getSalesChannel(), null, null, null, contractorPartNo, null});
                    } else {
                        Dossier dos = dossierEao.findById(stu.getLogicTransaction().getDossierId());
                        result.add(new Object[]{stockTaking, dos.isClosed() ? "abgeschlossen" : "in transfer", refurbishId,
                            partNo, uu.getSerial(), name, uu.getContractor(), stock, uu.getSalesChannel(), dos.getCrucialDirective().getName(),
                            dos.getCustomerId(), dos.getIdentifier(), contractorPartNo, customerService.asUiCustomer(dos.getCustomerId()).toNameCompanyLine()});
                    }
                }
            }
        }
        stockTaking = "nicht erfasst";
        m.message("lade fehlende Geräte");
        List<StockUnit> openUnits = (stockId == null ? stockUnitEao.findAll() : stockUnitEao.findByStockId(stockId));
        m.worked(8);
        openUnits.removeAll(found);
        m.setWorkRemaining(openUnits.size());
        for (StockUnit stu : openUnits) {
            m.worked(1, "vervollständige " + stu.getRefurbishId());
            UniqueUnit uu = uniqueUnitEao.findById(stu.getUniqueUnitId());
            String partNo = uu.getProduct().getPartNo();
            String contractorPartNo = uu.getProduct().getAdditionalPartNo(uu.getContractor());
            String name = ProductFormater.toName(uu.getProduct());

            // jetzt schauen was mit st ist
            String stock = (stu.getStock() == null ? stu.getTransaction().toString() : stu.getStock().getName());
            if ( stu.getLogicTransaction() == null ) {
                result.add(new Object[]{stockTaking, "verfügbar", uu.getRefurbishId(),
                    partNo, uu.getSerial(), name, uu.getContractor(), stock, uu.getSalesChannel(), null, null, null, contractorPartNo, null});
            } else {
                Dossier dos = dossierEao.findById(stu.getLogicTransaction().getDossierId());
                result.add(new Object[]{stockTaking, dos.isClosed() ? "abgeschlossen" : "in transfer", uu.getRefurbishId(),
                    partNo, uu.getSerial(), name, uu.getContractor(), stock, uu.getSalesChannel(), dos.getCrucialDirective().getName(),
                    dos.getCustomerId(), dos.getIdentifier(), contractorPartNo, customerService.asUiCustomer(dos.getCustomerId()).toNameCompanyLine()});
            }
        }
        //
        // Insert Open Reklas.
        //
        for (String error : read.errors) {
            result.add(new Object[]{"Lesefehler", error, null, null, null, null, null, null, null, null, null, null, null, null});
        }
        m.message("Erzeuge Tabelle");
        CSheet sheet = new CSheet("Inventur");
        STable table = new STable();
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, WHITE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Inventur", 12)).add(new STableColumn("Status", 10)).add(new STableColumn("SopoNr", 10)).add(new STableColumn("ArtikelNr", 16));
        table.add(new STableColumn("Seriennummer", 30)).add(new STableColumn("Name", 50)).add(new STableColumn("Contractor", 14)).add(new STableColumn("Lager", 25));
        table.add(new STableColumn("Verkaufskanal", 16)).add(new STableColumn("Directive", 20)).add(new STableColumn("Kid", 8)).add(new STableColumn("VorgangsId", 10));
        table.add(new STableColumn("LieferantenPartNo", 16)).add(new STableColumn("Kunde", 40));
        table.setModel(new STableModelList(result));
        sheet.addBelow(table);
        CCalcDocument document = new TempCalcDocument();
        document.add(sheet);
        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(document);
        m.finish();
        return new FileJacket("Inventur", ".xls", file);
    }

    ReaderResult xlsToList(FileJacket inFile) {
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class);
        reader.setHeadline(true);
        reader.setTrim(true);
        File f = inFile.toTemporaryFile();
        return new ReaderResult(reader.read(f), reader.getErrors());
    }

}
