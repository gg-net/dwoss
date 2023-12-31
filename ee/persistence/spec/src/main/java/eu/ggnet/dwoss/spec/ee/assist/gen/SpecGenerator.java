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
package eu.ggnet.dwoss.spec.ee.assist.gen;

import java.io.*;
import java.util.List;
import java.util.Random;

import eu.ggnet.dwoss.spec.ee.eao.CpuEao;
import eu.ggnet.dwoss.spec.ee.eao.GpuEao;
import eu.ggnet.dwoss.spec.ee.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.ee.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import jakarta.xml.bind.JAXB;

import eu.ggnet.dwoss.spec.ee.entity.xml.SpecsRoot;

import jakarta.persistence.EntityManager;

/**
 * SpecGenerator, creates instances without changing any persistence layer.
 * <p>
 * The two different ways to initialize are here just for example. In use will only be one. And after doing the complicated part with XML,
 * the XML implementation will be used. But for the next scenarios like these, just start with the direct serialized.
 *
 * @author oliver.guenther
 */
public class SpecGenerator {

    private List<? extends ProductSpec> productSpecs;

    private final Random R = new Random();

    private synchronized void initXml() {
        if ( productSpecs != null && !productSpecs.isEmpty() ) return;
        try (InputStream is = getClass().getResourceAsStream("specs.xml")) {
            SpecsRoot root = JAXB.unmarshal(is, SpecsRoot.class);
            productSpecs = root.getProductSpecs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void initSerialized() {
        if ( productSpecs != null && !productSpecs.isEmpty() ) return;
        try (InputStream is = getClass().getResourceAsStream("specs.ser"); ObjectInputStream ois = new ObjectInputStream(is)) {
            SpecsRoot root = (SpecsRoot)ois.readObject();

            productSpecs = root.getProductSpecs();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write ProductSpecs to a new XML file.
     * <p>
     * @param filename     the filename
     * @param productSpecs the specs
     */
    public static void writeXml(String filename, List<? extends ProductSpec> productSpecs) {
        try (FileWriter fw = new FileWriter(filename)) {
            JAXB.marshal(new SpecsRoot(productSpecs), fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write ProductSpecs to a new serialized file.
     * <p>
     * @param filename     the filename
     * @param productSpecs the specs
     */
    public static void writeSerialized(String filename, List<? extends ProductSpec> productSpecs) {
        try {
            try (FileOutputStream fileOut = new FileOutputStream(filename); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(new SpecsRoot(productSpecs));
            }
        } catch (IOException i) {
            throw new RuntimeException(i);
        }
    }

    /**
     * Makes a Semi random ProductSpec, no Persistence Change.
     * <p>
     * @return the spec.
     */
    public ProductSpec makeSpec() {
        initXml();
        return productSpecs.remove(R.nextInt(productSpecs.size()));
    }

    public ProductSpec makeRandom(EntityManager specEm) {
        ProductSpec spec = makeSpec();
        ProductModel model = spec.getModel();
        ProductModelEmo productModelEmo = new ProductModelEmo(specEm);
        model = productModelEmo.request(model.getFamily().getSeries().getBrand(), model.getFamily().getSeries().getGroup(), model.getFamily().getSeries().getName(), model.getFamily().getName(), model.getName());
        spec.setModel(model);
        if ( spec instanceof DisplayAble ) {
            DisplayAble da = (DisplayAble)spec;
            da.setDisplay(new DisplayEmo(specEm).weakRequest(da.getDisplay().getSize(), da.getDisplay().getResolution(), da.getDisplay().getType(), da.getDisplay().getRation()));
        }
        if ( spec instanceof Desktop ) {
            Desktop desktop = (Desktop)spec;
            if ( desktop.getCpu() == null || desktop.getGpu() == null ) throw new IllegalArgumentException("Cpu or Gpu of a Desktop are null. " + desktop);
            Cpu cpu = new CpuEao(specEm).findById(desktop.getCpu().getId());
            Gpu gpu = new GpuEao(specEm).findById(desktop.getGpu().getId());
            if ( cpu != null ) desktop.setCpu(cpu);
            if ( gpu != null ) desktop.setGpu(gpu);
        }
        specEm.persist(spec);
        return spec;
    }

}
