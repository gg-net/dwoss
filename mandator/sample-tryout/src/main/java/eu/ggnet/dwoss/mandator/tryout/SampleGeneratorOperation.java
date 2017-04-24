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
package eu.ggnet.dwoss.mandator.tryout;

import java.io.Serializable;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGeneratorOperation;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.eao.StockEao;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;

/**
 * Sample Genaerator.
 * If the connected databases are empty this class will generate random entities and persist them.
 *
 * @author oliver.guenther
 */
@Startup
@Singleton
public class SampleGeneratorOperation implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(SampleGeneratorOperation.class);

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private ReportLineGeneratorOperation reportLineGeneratorRemote;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @Inject
    private RightsGeneratorOperation rightsGenerator;

    @Inject
    private StockEao stockEao;

    @Inject
    private CustomerEao customerEao;

    @Inject
    private UniqueUnitEao uniqueUnitEao;

    @Inject
    private DossierEao dossierEao;

    @Inject
    private ReportLineEao reportLineEao;

    public void generateSampleData() {
        if ( stockEao.count() == 0 && customerEao.count() == 0 && uniqueUnitEao.count() == 0 && dossierEao.count() == 0 && reportLineEao.count() == 0 ) {
            LOG.info("Generating Persistence Data");
            rightsGenerator.make("admin", "admin", 123, EnumSet.allOf(AtomicRight.class));
            rightsGenerator.make("user", EnumSet.noneOf(AtomicRight.class));
            stockGenerator.makeStocksAndLocations(2);
            customerGenerator.makeCustomers(100);
            receiptGenerator.makeProductSpecs(30, true);
            receiptGenerator.makeUniqueUnits(200, true, true);
            redTapeGenerator.makeSalesDossiers(50);
            reportLineGeneratorRemote.makeReportLines(500);
            LOG.info("Persistence Data generated");
            return;
        }

        // We have some errors, lets help the user.
        if ( stockEao.count() > 0 ) error("Stock is not empty, disabling data generation");
        if ( customerEao.count() > 0 ) error("Customer is not empty, disabling data generation");
        if ( uniqueUnitEao.count() > 0 ) error("UniqueUnit is not empty, disabling data generation");
        if ( dossierEao.count() > 0 ) error("RedTape is not empty, disabling data generation");
        if ( reportLineEao.count() > 0 ) error("Report is not empty, disabling data generation");
    }

    private void error(String msg) {
        LOG.error(msg);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("", msg));
    }

    @PostConstruct
    public void init() {
        generateSampleData();
    }

}
