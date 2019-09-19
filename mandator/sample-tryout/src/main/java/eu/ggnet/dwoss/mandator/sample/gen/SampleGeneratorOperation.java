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
package eu.ggnet.dwoss.mandator.sample.gen;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

import javax.ejb.*;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.gen.Assure;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtapext.ee.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGeneratorOperation;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;

import static javax.ejb.LockType.READ;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

/**
 * Sample Genaerator.
 * If the connected databases are empty this class will generate random entities and persist them.
 *
 * @author oliver.guenther
 */
@Singleton
public class SampleGeneratorOperation implements Serializable {

    private static final Logger L = LoggerFactory.getLogger(SampleGeneratorOperation.class);

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

    @Inject
    private MonitorFactory monitorFactory;
    
    @Inject
    private Mandator mandator;

    /**
     * If true this generator is generating samples.
     */
    private boolean generating = false;

    /**
     * If true this generator has completed generating samples.
     */
    private boolean generated = false;

    @Lock(READ)
    public boolean isGenerating() {
        return generating;
    }

    @Lock(READ)
    public boolean isGenerated() {
        return generated;
    }

    // TODO: Super candidat für Background opperation. Dann kann man das generated auch über ein Future lösen :-)
    @Lock(READ)
    @Asynchronous
    @TransactionAttribute(REQUIRES_NEW)
    public void generateSampleData() {
        if ( stockEao.count() == 0 && customerEao.count() == 0 && uniqueUnitEao.count() == 0 && dossierEao.count() == 0 && reportLineEao.count() == 0 ) {
;
            SubMonitor m = monitorFactory.newSubMonitor("Data generator", 7);
            m.start();
            generating = true;
            L.info("Generating Persistence Data");
            m.message("Generating Users");
            rightsGenerator.make("admin", "admin", 123, EnumSet.allOf(AtomicRight.class));
            rightsGenerator.make("user", EnumSet.noneOf(AtomicRight.class));
            m.worked(1, "Generating Stocks");
            stockGenerator.makeStocksAndLocations(2);
            m.worked(1, "Generating 300 Customers");
            customerGenerator.makeCustomers(50);
            customerGenerator.makeCustomers(50, new Assure.Builder().simple(true).build());
            customerGenerator.makeCustomers(50, new Assure.Builder().simple(true).addAllMandatorMetadataMatchCodes(Arrays.asList(mandator.matchCode())).build());
            customerGenerator.makeCustomers(50, new Assure.Builder().addAllMandatorMetadataMatchCodes(Arrays.asList(mandator.matchCode())).build());
            customerGenerator.makeCustomers(100, new Assure.Builder().addAllMandatorMetadataMatchCodes(Arrays.asList(mandator.matchCode(),RandomStringUtils.randomAlphabetic(5).toUpperCase())).build());
            m.worked(1, "Generating 100 Specs");
            receiptGenerator.makeProductSpecs(100, true);
            m.worked(1, "Generating 200 Units");
            receiptGenerator.makeUniqueUnits(200, true, true);
            m.worked(1, "Generating 50 Dossiers");
            redTapeGenerator.makeSalesDossiers(50);
            m.worked(1, "Generating 500 Reportlines");
            reportLineGeneratorRemote.makeReportLines(500);
            L.info("Persistence Data generated");
            generating = false;
            generated = true;
            m.finish();
            return;
        }

        generating = false;
        // We have some errors, lets help the user.
        if ( stockEao.count() > 0 ) error("Stock is not empty, disabling data generation");
        if ( customerEao.count() > 0 ) error("Customer is not empty, disabling data generation");
        if ( uniqueUnitEao.count() > 0 ) error("UniqueUnit is not empty, disabling data generation");
        if ( dossierEao.count() > 0 ) error("RedTape is not empty, disabling data generation");
        if ( reportLineEao.count() > 0 ) error("Report is not empty, disabling data generation");
    }

    private void error(String msg) {
        L.error(msg);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("", msg));
    }


}
