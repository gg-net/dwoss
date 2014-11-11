/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.assembly.web;

import java.io.*;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.*;

import org.slf4j.*;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGeneratorOperation;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;

import lombok.Getter;

/**
 *
 * @author oliver.guenther
 */
@Named
@ApplicationScoped
public class Overview implements Serializable {

    static final Logger LOG = LoggerFactory.getLogger(Overview.class);

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

    @Getter
    private boolean sample = false;

    public List<String> getStatus() {
        return Arrays.asList("Running", "As Sample:" + sample);
    }

    public void generateSampleData() {
        if ( !sample ) {
            LOG.error("Sample data generation in productive environment not allowed");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("", "Sample data generation in productive environment not allowed"));
            return;
        }
        stockGenerator.makeStocksAndLocations(2);
        customerGenerator.makeCustomers(100);
        receiptGenerator.makeProductSpecs(30, true);
        receiptGenerator.makeUniqueUnits(200, true, true);
        redTapeGenerator.makeSalesDossiers(50);
        reportLineGeneratorRemote.makeReportLines(500);
    }

    @PostConstruct
    public void init() {
        Properties prop = new Properties();
        try {
            prop.load(Overview.class.getClassLoader().getResourceAsStream("dw-web.properties"));
            sample = Boolean.valueOf(prop.getProperty("isSample"));
            LOG.info("Sampleclient = " + sample);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to load properties.", ex);
        }
    }

}
