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
package eu.ggnet.dwoss.misc.op;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.eao.LogicTransactionEao;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.STableColumn;

import java.io.File;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.swing.JOptionPane;
import javax.validation.*;

import eu.ggnet.dwoss.redtape.eao.DossierEao;


import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;

import lombok.*;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static java.awt.Color.*;

@Stateless
public class PersistenceValidatorOperation implements PersistenceValidator {

    @Data
    public static class Vm {

        @RequiredArgsConstructor
        private static enum Level {

            ERROR("Fehler"), WARNING("Warnung");

            @Getter
            private final String name;
        }

        private final Level level;

        private final String message;
    }

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private MonitorFactory monitorFactory;

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * This Method Validate all Databases.
     * It's validate:
     * - RedTape
     * - UniqueUnit
     * - Sopo
     * - Stock
     * <p/>
     * @return a Filejacket where a xls from the JExcel api is, that contains all Errors.
     */
    @Override
    public FileJacket validateDatabase() {
        List<Vm> vms = new ArrayList<>();
        UniqueUnitEao uuEao = new UniqueUnitEao(uuEm);
        DossierEao dossierEao = new DossierEao(redTapeEm);
        LogicTransactionEao logicEao = new LogicTransactionEao(stockEm);
        StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        int uuMax = uuEao.count();
        int dossierMax = dossierEao.count();
        int logicMax = logicEao.count();
        int stockUnitMax = stockUnitEao.count();
        SubMonitor m = monitorFactory.newSubMonitor("DatenbankValidation", 100);
        //All Listen
        List<Dossier> dossiers;
        List<UniqueUnit> uniqueUnits;
        List<LogicTransaction> logicTransactions;
        List<StockUnit> stockUnits;
        m.message("Hole alle Unique Units. " + uuMax);
        uniqueUnits = uuEao.findAll();
        m.worked(10, "Hole Alle Dossiers. " + dossierMax);
        dossiers = dossierEao.findAll();
        m.worked(10, "Hole Alle LogicTransaction. " + logicMax);
        logicTransactions = logicEao.findAll();
        m.worked(10, "Hole Alle StockUnit. " + stockUnitMax);
        stockUnits = stockUnitEao.findAll();
        m.worked(10, "Validieren.");
        validateRedTape(vms, dossiers, convertUnitListToMap(uniqueUnits), m.newChild(10));
        validateUniqueUnit(vms, uniqueUnits, m.newChild(10));
        validateLogicTransaction(vms, logicTransactions, dossiers, m.newChild(10));
        // TODO: split:
        // 1. Sopo Validation <-> RedTape
        // 2. Sopo Only.
        m.finish();
        return createFileJacket(vms);
    }

    // HINT: See validate in RedTapeUpdateWorkflow.
    /**
     * This method checks given Dossiers.
     * It checks followed things:
     * - It is only one Document of every existing Type active but it must one Document active.
     * - Every Position from type UNIT have a existing UniqueUnit.
     * - It exist a SopoAuftrag to a Dossier. <---- TODO: It must a status of a Dossier where a SopoAuftrag exist, not Canceled ---->
     * - and a BothSide check if the Positions of a Dossier are in the SopoAuftrag and vice vesa.
     * <p/>
     * @param dossiers
     * @param units    Map&lt;uniqueUnitId, uniqueUnits&rt;
     * @param auftrags
     * @param m
     * @return
     */
    private void validateRedTape(List<Vm> vms, List<Dossier> dossiers, Map<Integer, UniqueUnit> units, SubMonitor m) {
        m.setWorkRemaining(dossiers.size());
        m.start();
        for (Dossier dossier : dossiers) {
            m.worked(1, "Validate: Dossier:" + dossier.getId());

            Set<ConstraintViolation<Dossier>> validateError = validator.validate(dossier);
            if ( !validateError.isEmpty() ) {
                error(vms, ConstraintViolationFormater.toSingleLine(validateError));
                continue;
            }
            for (DocumentType type : DocumentType.values()) {
                Set<Document> documents = dossier.getDocuments();
                List<Document> existingDocumentWithType = new ArrayList<>();
                for (Document document : documents) {
                    if ( document.getType() == type ) existingDocumentWithType.add(document);
                }
                if ( !existingDocumentWithType.isEmpty() && !dossier.getActiveDocuments(type).isEmpty()
                        && dossier.getActiveDocuments(type).size() > 1 )
                    error(vms, "Dossier(id=" + dossier.getId() + ",customerId=" + dossier.getCustomerId() + "): Es ist mehr als ein Dokument vom Typ " + type.getName() + " ist active.");
            }
            for (Document document : dossier.getDocuments()) {
                for (Position position : document.getPositions(PositionType.UNIT).values()) {
                    if ( !units.containsKey(position.getUniqueUnitId()) ) {
                        error(vms, "Dossier(id=" + dossier.getId() + ",customerId=" + dossier.getCustomerId() + ") enhält Position(type=Unit,uniqueUnitId=" + position.getUniqueUnitId() + "): UniqueUnit existiert nicht.");
                    }
                }
            }
            if ( !dossier.getActiveDocuments(DocumentType.ORDER).isEmpty()
                    && dossier.getActiveDocuments(DocumentType.ORDER).get(0).getConditions().contains(Document.Condition.CANCELED) ) {
                continue;
            } else if ( dossier.isClosed() ) {
                continue;
            }
        }
        m.finish();
    }

    /**
     * This Method Check the given UniqueUnit list.
     * First check is if a UniqueUnit has a Product and the Productdescription isn't null.
     * If the Description isn't null it checks if it contains a invalid Character.
     * <p/>
     * @param units
     * @param m
     * @return
     */
    private void validateUniqueUnit(List<Vm> vms, List<UniqueUnit> units, SubMonitor m) {
        m.setWorkRemaining(units.size());
        m.start();
        for (UniqueUnit uniqueUnit : units) {
            m.worked(1, "Validate: UniqueUnit:" + uniqueUnit.getId());
            Set<ConstraintViolation<UniqueUnit>> validateError = validator.validate(uniqueUnit);
            if ( !validateError.isEmpty() ) {
                error(vms, ConstraintViolationFormater.toSingleLine(validateError));
            }
            if ( uniqueUnit.getProduct() == null ) {
                warn(vms, "UniqueUnit(id=" + uniqueUnit.getId() + ",refurbishId=" + uniqueUnit.getRefurbishId() + ").product == null");
            } else if ( uniqueUnit.getProduct().getDescription() == null ) {
                warn(vms, "UniqueUnit(id=" + uniqueUnit.getId() + ",refurbishId=" + uniqueUnit.getRefurbishId() + ").product(id=" + uniqueUnit.getProduct().getId() + ",partNo=" + uniqueUnit.getProduct().getPartNo() + ").description == null");
            }
        }
        m.finish();
    }

    /**
     *
     * This Method Validate all LogicTransaction that will be given.
     * First it checks if all UUIds from the Document are in the Logictransaction.
     * Then check its in the opposite way.
     * <p/>
     * @param transactions
     * @param dossiers
     * @param m
     * @return
     */
    private void validateLogicTransaction(List<Vm> vms, List<LogicTransaction> transactions, List<Dossier> dossiers, SubMonitor m) {
        Map<Long, Dossier> dossierMap = new HashMap<>();
        m.setWorkRemaining(transactions.size());
        for (Dossier dossier : dossiers) {
            dossierMap.put(dossier.getId(), dossier);
        }
        for (LogicTransaction logicTransaction : transactions) {
            m.worked(1, "Validate: LogicTransaction:" + logicTransaction.getId());
            Dossier dossier = dossierMap.get(logicTransaction.getDossierId());

            DocumentType type = getMostImportandDocument(dossierMap.get(logicTransaction.getDossierId())).getType();
            // TODO: Here you discard cases, not good.
            if ( type != DocumentType.INVOICE && type != DocumentType.ORDER ) {
                continue;
            }
            List<Integer> stockUuIds = toUniqueUnitIds(logicTransaction);
            if ( !stockUuIds.containsAll(dossier.getRelevantUniqueUnitIds()) ) {
                error(vms, "Stock asynchron zu Dossier. LogicTransaction(id=" + logicTransaction.getId() + ", UniqueUnits="
                        + logicTransaction.getUnits() + ") ->" + "Dossier( id=" + dossier.getId() + ",customerId=" + dossier.getCustomerId() + ", relevant UniqueUnits="
                        + dossier.getRelevantUniqueUnitIds() + ")");

            }
            if ( !dossier.getRelevantUniqueUnitIds().containsAll(stockUuIds) ) {
                error(vms, "Dossier asynchron zu Stock." + "Dossier(id=" + dossier.getId() + ",customerId=" + dossier.getCustomerId() + ",relevant UniqueUnits="
                        + dossier.getRelevantUniqueUnitIds() + ") -> LogicTransaction(id=" + logicTransaction.getId() + ", UniqueUnits="
                        + logicTransaction.getUnits() + ")");
            }
            for (StockUnit stockUnit : logicTransaction.getUnits()) {
                Set<ConstraintViolation<StockUnit>> validateError = validator.validate(stockUnit);
                if ( !validateError.isEmpty() ) {
                    error(vms, ConstraintViolationFormater.toSingleLine(validateError));
                }
            }
        }
        m.finish();
    }

    /** TODO: SHOULD BE TESTED IF ITS WORKS CORRECTLY!
     * MID / VIP
     * <p/>
     * @param dossier
     * @return
     */
    private Document getMostImportandDocument(Dossier dossier) {
        List<Document> activeDocuments = dossier.getActiveDocuments();
        Document activeDocument = null;
        for (Document document : activeDocuments) {
            if ( activeDocument == null ) {
                activeDocument = document;
                continue;
            }
            if ( activeDocument.getType().compareTo(document.getType()) > 0 )
                activeDocument = document;
        }
        return activeDocument;
    }

    /**
     * Convert a List of SopoUnits to a map with UniqueUnit id as Key.
     * <p/>
     * @param units
     * @return
     */
    private Map<Integer, StockUnit> stockToUniqueUnitId(Collection<StockUnit> units) {
        Map<Integer, StockUnit> map = new HashMap<>();
        for (StockUnit stockUnit : units) {
            map.put(stockUnit.getUniqueUnitId(), stockUnit);
        }
        return map;
    }

    /**
     * Make out of List of units a Map with UnitId as Key
     * <p/>
     * @param units
     * @return
     */
    private Map<Integer, UniqueUnit> convertUnitListToMap(Collection<UniqueUnit> units) {
        Map<Integer, UniqueUnit> map = new HashMap<>();
        for (UniqueUnit uniqueUnit : units) {
            map.put(uniqueUnit.getId(), uniqueUnit);
        }
        return map;
    }

    /**
     * This method make a List of UniqueUnitIds out of the given LogicTransaction.
     * <p/>
     * @param logicTransaction
     * @return
     */
    private List<Integer> toUniqueUnitIds(LogicTransaction logicTransaction) {
        List<Integer> stockUuIds = new ArrayList<>(logicTransaction.getUnits().size());
        for (StockUnit stockUnit : logicTransaction.getUnits()) {
            stockUuIds.add(stockUnit.getUniqueUnitId());
        }
        return stockUuIds;
    }

    /**
     * This Method create a XLS Document with the given list of errors.
     * First it sort the erros by his level and put them into different sheets.
     * <p/>
     * @param errors
     * @return
     */
    private FileJacket createFileJacket(List<Vm> errors) {
        if ( errors.isEmpty() ) {
            JOptionPane.showMessageDialog(null, "Kein Fehler gefunden");
            return null;
        }
        List<Object[]> rows = new ArrayList<>();
        for (Vm vm : errors) {
            rows.add(new Object[]{vm.getLevel(), vm.getMessage()});
        }
        CSheet sheet = new CSheet("Fehler");
        STable table = new STable();
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, RED, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("Level", 12, new CFormat(RED, BLUE))).add(new STableColumn("Nachricht", 60));
        table.setModel(new STableModelList(rows));
        sheet.addBelow(table);

        CCalcDocument document = new TempCalcDocument();
        document.add(sheet);
        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(document);
        FileJacket result = new FileJacket("Datenbank_Errors", ".xls", file);
        return result;
    }

    private void error(List<Vm> vms, String msg) {
        if ( msg == null ) return;
        if ( msg.trim().equals("") ) return;
        vms.add(new Vm(Vm.Level.ERROR, msg));
    }

    private void warn(List<Vm> vms, String msg) {
        if ( msg == null ) return;
        if ( msg.trim().equals("") ) return;
        vms.add(new Vm(Vm.Level.WARNING, msg));
    }
}
