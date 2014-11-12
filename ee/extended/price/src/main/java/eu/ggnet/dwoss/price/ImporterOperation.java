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
package eu.ggnet.dwoss.price;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.lucidcalc.LucidCalcReader;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * The ImportLogic for PriceManagement Files
 *
 * @author oliver.guenther
 */
@Stateless
@NoArgsConstructor
@AllArgsConstructor
public class ImporterOperation implements Importer {

    @Inject
    private PriceCoreOperation core;

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * Imports the Pricemanagement from an XLS file with a defined form.
     * The Form is as follows
     * <ul>
     * <li>Column 1 (A) = Refurbished Id, Type:Integer</li>
     * <li>Column 2 (C) = Manufacturer PartNo, Type:String</li>
     * <li>Column 4 (E) = Retailer Price, Type:Double</li>
     * <li>Column 7 (H) = Customer Price without Tax, Type:Double</li>
     * <li>Column 9 (J) = Set/Unset PartNoFixed Price, Type:Integer</li>
     * <li>Column 10 (K) = Warranty Id, Type:Integer</li>
     * </ul>
     *
     * @param jacket  the file in a jacket
     * @param monitor an optional monitor
     * @throws UserInfoException
     */
    @Override
    public void fromXls(FileJacket jacket, String arranger) throws UserInfoException {
        final SubMonitor m = monitorFactory.newSubMonitor("Import from Xls", 10);
        m.start();
        File f = jacket.toTemporaryFile();
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class); // RefurbishedId
        reader.addColumn(2, String.class); // PartNo
        reader.addColumn(4, Double.class); // RetailerPrice
        reader.addColumn(7, Double.class); // CustomerPrice
        reader.addColumn(9, Integer.class); // UnitFixPrice
        reader.addColumn(10, Integer.class); // PartFixPrice
        reader.addColumn(11, Integer.class); // WarrantyId
        List<PriceEngineResult> imports = reader.read(f, new PriceEngineResult());
        m.worked(2);
        if ( reader.isError() ) {
            m.finish();
            throw new UserInfoException(reader.getErrors());
        }
        core.store(imports, "ImportPriceManagementOperation.fromXls()", arranger, m);
    }

    /**
     * Uses the Engine in the Background, and imports all Prices direct.
     * <p/>
     * @param arranger the arranger.
     */
    @Override
    public void direct(String arranger) {
        SubMonitor m = monitorFactory.newSubMonitor("Preise erzeugen und importieren", 100);
        List<PriceEngineResult> pers = core.loadAndCalculate(m.newChild(60));
        core.store(pers, "PriceUtilOperation.directExportImport()", arranger, m);
        m.finish();
    }

    /**
     * Store one price result in the Price Engine.
     * <p/>
     * @param pers     the price result
     * @param comment  a optional comment
     * @param arranger the arranger
     */
    @Override
    public void store(PriceEngineResult pers, String comment, String arranger) {
        SubMonitor m = monitorFactory.newSubMonitor("Preis einspielen", 10);
        core.store(Arrays.asList(pers), comment, arranger, m);
        m.finish();
    }
}
