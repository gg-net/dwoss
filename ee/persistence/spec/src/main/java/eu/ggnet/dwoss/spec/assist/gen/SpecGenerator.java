package eu.ggnet.dwoss.spec.assist.gen;

import java.io.*;
import java.util.*;

import javax.xml.bind.JAXB;

import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.entity.xml.SpecsRoot;

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
        try (InputStream is = getClass().getResourceAsStream("specs.ser");
                ObjectInputStream ois = new ObjectInputStream(is)) {
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
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
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

    public static void main(String[] args) {
        SpecGenerator g = new SpecGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(g.makeSpec());
        }
    }
}
