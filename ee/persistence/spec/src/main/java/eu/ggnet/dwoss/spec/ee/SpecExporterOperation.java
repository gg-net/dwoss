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
package eu.ggnet.dwoss.spec.ee;

import java.io.*;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXB;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.ee.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.entity.xml.SpecsRoot;
import eu.ggnet.dwoss.util.FileJacket;

/**
 * Operation to allow the XML Export of multiple ProductSpecs.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class SpecExporterOperation implements SpecExporter {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private MonitorFactory monitorFactory;

    /**
     * Exports specs to an XML till the supplied amount.
     * <p>
     * @param amount the amount to export
     * @return a FileJacket containing all the found specs.
     */
    @Override
    public FileJacket toXml(int amount) {
        SubMonitor m = monitorFactory.newSubMonitor("Export ProductSpecs", amount + 10);
        m.start();
        m.message("init");
        ProductSpecEao specEao = new ProductSpecEao(em);
        int count = specEao.count();
        m.worked(2);
        if ( count < amount ) {
            m.setWorkRemaining(count + 8);
            amount = count;
        }
        int step = 10; // load in the batches of 10
        List<ProductSpec> exportSpecs = new ArrayList<>();
        for (int i = 0; i <= amount; i = i + step) {
            m.worked(step, "loading " + step + " Spec beginning by " + i);
            for (ProductSpec spec : specEao.findAll(i, step)) {
                if ( spec instanceof DesktopBundle ) continue;
                exportSpecs.add(spec);
            }
        }
        try {
            File f = File.createTempFile("specs", ".xml");
            try (OutputStream fw = new BufferedOutputStream(new FileOutputStream(f))) {
                m.message("marschaling");
                JAXB.marshal(new SpecsRoot(exportSpecs), fw);
            }
            return new FileJacket("specs", ".xml", f);
        } catch (IOException ex) {
            throw new RuntimeException("", ex);
        } finally {
            m.finish();
        }
    }
}
