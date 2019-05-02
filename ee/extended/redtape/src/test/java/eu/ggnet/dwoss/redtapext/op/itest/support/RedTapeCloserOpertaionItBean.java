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
package eu.ggnet.dwoss.redtapext.op.itest.support;

import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.redtapext.op.itest.RedTapeCloserOperationIT;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Assert;

import eu.ggnet.dwoss.redtapext.op.itest.RedTapeCloserOperationOfWarrantyIT;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

/**
 *
 * @author olive
 */
@Stateless
public class RedTapeCloserOpertaionItBean {

    @Inject
    private ReportLineEao reportEao;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    public void checkReferences(long dossierId) {
        List<ReportLine> allLines = reportEao.findAll();
        List<ReportLine> collect = allLines.stream().filter((eu.ggnet.dwoss.report.ee.entity.ReportLine line) -> {
            return line.getPositionType().equals(PositionType.PRODUCT_BATCH) && line.getDossierId() == dossierId;
        }).collect(Collectors.toList());
        Assert.assertEquals("Assert ten warranties to be present", 2, collect.size());
        for (ReportLine line : collect) {
            ReportLine reference = line.getReference(SingleReferenceType.WARRANTY);
            Assert.assertFalse("Line has no unit reference " + line, reference == null);
            Assert.assertEquals("Assert equal dossier id in reference", reference.getDossierId(), dossierId);
            Assert.assertFalse("Contractor has not been set", reference.getContractor() == null);
        }
    }

    public Product makeWarrantyProduct(String partNo) {
        Product p = new Product(ProductGroup.COMMENTARY, TradeName.HP, partNo, "Warranty Product");
        uuEm.persist(p);
        return p;
    }

    //delete Stockunit and logictransaction for specific stockunit
    public void deleteStockUnit(int stockUnitId) {
        StockUnit unit = stockEm.find(StockUnit.class, stockUnitId);
        LogicTransaction transaction = stockEm.find(LogicTransaction.class, unit.getLogicTransaction().getId());
        stockEm.remove(unit);
        stockEm.remove(transaction);
    }

}
