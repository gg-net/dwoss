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
package eu.ggnet.dwoss.receipt.ee;

import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.SpecPu;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.*;
import eu.ggnet.dwoss.spec.ee.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.ee.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

/**
 * This Logic is used in Reciept to handle all modifications to ProductSpec,Product and SopoProduct.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductProcessorOperation implements ProductProcessor {

    private final static Logger L = LoggerFactory.getLogger(ProductProcessorOperation.class);

    @Inject
    @Specs
    private EntityManager specEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    /**
     * Creates a new Cpu instance.
     *
     * @param cpu the Cpu to create
     * @return the Cpu instance from databes, but detached.
     * @throws IllegalArgumentException if there exist a cpu with the same series and the name.
     */
    @Override
    public Cpu create(Cpu cpu) throws IllegalArgumentException {
        EntityManager em = specEm;
        CpuEao cpuEao = new CpuEao(specEm);
        Cpu existendCpu = cpuEao.find(cpu.getSeries(), cpu.getModel());
        if ( existendCpu != null ) throw new IllegalArgumentException("Cpu with same Series and Name exists " + existendCpu);
        em.persist(cpu);
        return cpu;
    }

    /**
     * Updates an existing detached Cpu.
     *
     * @param cpu the detached Cpu
     * @return the reatached updated Cpu
     * @throws IllegalArgumentException if there allready exists a Cpu with the same series and name
     */
    @Override
    public Cpu update(Cpu cpu) throws IllegalArgumentException {
        EntityManager em = specEm;
        CpuEao cpuEao = new CpuEao(em);
        Cpu existendCpu = cpuEao.find(cpu.getSeries(), cpu.getModel());
        if ( existendCpu != null && existendCpu.getId() != cpu.getId() )
            throw new IllegalArgumentException("Cpu with same Series and Name exists " + existendCpu);

        return em.merge(cpu);
    }

    /**
     * Creates a new Gpu instance.
     *
     * @param gpu the Gpu to create
     * @return the Gpu instance from databes, but detached.
     * @throws IllegalArgumentException if there exist a Gpu with the same series and the name.
     */
    @Override
    public Gpu create(Gpu gpu) throws IllegalArgumentException {
        EntityManager em = specEm;
        GpuEao gpuEao = new GpuEao(em);
        Gpu existendGpu = gpuEao.find(gpu.getSeries(), gpu.getModel());
        if ( existendGpu != null ) throw new IllegalArgumentException("Gpu with same Series and Name exists " + existendGpu);
        em.persist(gpu);
        return gpu;
    }

    /**
     * Updates an existing detached Gpu.
     *
     * @param gpu the detached Gpu
     * @return the reatached updated Gpu
     * @throws IllegalArgumentException if there allready exists a Gpu with the same series and name
     */
    @Override
    public Gpu update(Gpu gpu) throws IllegalArgumentException {
        EntityManager em = specEm;
        GpuEao gpuEao = new GpuEao(em);
        Gpu existendGpu = gpuEao.find(gpu.getSeries(), gpu.getModel());
        if ( existendGpu != null && existendGpu.getId() != gpu.getId() )
            throw new IllegalArgumentException("Gpu with same Series and Name exists " + existendGpu);
        return em.merge(gpu);
    }

    /**
     * Creates a new ProductModel and Persists it.
     * Multistep Process:
     * <ol>
     * <li>Validate and throw exception if a model with the same name exists</li>
     * <li>Selection of Family and Series
     * <ul>
     * <li>If Family is null and Series is null &rarr; find or create default Series and default Family</li>
     * <li>If only Family is null &rarr; find or create default default Family for Series</li>
     * <li>Else use supplied Family</li>
     * </ul>
     * </li>
     * <li>Persist new Model with Family</li>
     * </ol>
     *
     * @param brand     must not be null
     * @param group     must not be null
     * @param series    if null, default is used
     * @param family    if null, default is used
     * @param modelName the name of the model
     *
     * @return the persisted and detached Model
     */
    @Override
    public ProductModel create(final TradeName brand, final ProductGroup group, ProductSeries series, ProductFamily family, final String modelName) {
        EntityManager em = specEm;
        // 1. check if there exits a model anytwhere with the same name. -> throw Exception
        ProductModelEao modelEao = new ProductModelEao(em);
        ProductModel model = modelEao.find(modelName);
        if ( model != null ) throw new RuntimeException("There exits a model " + model + " but we want to create it");
        // 2. Select Family and Series
        if ( family != null && family.getId() != 0 ) {
            ProductFamilyEao familyEao = new ProductFamilyEao(em);
            family = familyEao.findById(family.getId());
        } else {
            if ( series != null && series.getId() != 0 ) {
                ProductSeriesEao seriesEao = new ProductSeriesEao(em);
                series = seriesEao.findById(series.getId());
            } else {
                ProductSeriesEao seriesEao = new ProductSeriesEao(em);
                series = seriesEao.find(brand, group, SpecPu.DEFAULT_NAME);
                if ( series == null ) {
                    series = new ProductSeries(brand, group, SpecPu.DEFAULT_NAME);
                    em.persist(series);
                }
            }
            for (ProductFamily f : series.getFamilys()) {
                if ( f.getName().equals(SpecPu.DEFAULT_NAME) ) {
                    family = f;
                }
            }
            if ( family == null ) {
                family = new ProductFamily(SpecPu.DEFAULT_NAME);
                family.setSeries(series);
                em.persist(family);
            }
        }
        // 3. Create Model
        model = new ProductModel(modelName);
        model.setFamily(family);
        em.persist(model);
        return model;
    }

    /**
     * Creates and Persists a ProducFamily.
     * <ol>
     * <li>Validate and throw exception if a family with the same name exists</li>
     * <li>Selection of Series
     * <ul>
     * <li>If Series is null &rarr; find or create default Series</li>
     * <li>Else use supplied Series</li>
     * </ul>
     * </li>
     * <li>Persist new Family with Series</li>
     * </ol>
     *
     * @param brand      the brand
     * @param group      the group
     * @param series     the series
     * @param familyName the familyName
     *
     * @return the persisted and detached ProductFamily
     */
    @Override
    public ProductFamily create(final TradeName brand, final ProductGroup group, ProductSeries series, final String familyName) {
        EntityManager em = specEm;
        // 1. check if there exits a model anytwhere with the same name. -> throw Exception
        ProductFamilyEao familyEao = new ProductFamilyEao(em);
        ProductFamily family = familyEao.find(familyName);
        if ( family != null ) throw new RuntimeException("There exits a family " + family + " but we want to create it");
        // 2. Create Family
        family = new ProductFamily(familyName);
        if ( series != null && series.getId() != 0 ) {
            // 3. if series not null, get from db
            ProductSeriesEao seriesEao = new ProductSeriesEao(em);
            series = seriesEao.findById(series.getId());
        } else {
            // 4. get or create default series
            ProductSeriesEao seriesEao = new ProductSeriesEao(em);
            series = seriesEao.find(brand, group, SpecPu.DEFAULT_NAME);
            if ( series == null ) {
                series = new ProductSeries(brand, group, SpecPu.DEFAULT_NAME);
                em.persist(series);
            }
        }
        family.setSeries(series);
        em.persist(family);
        return family;
    }

    @Override
    public ProductSpec create(SpecAndModel sam) throws IllegalArgumentException {
        Objects.requireNonNull(sam, "Spec and Model must not be null");
        ProductSpec spec = sam.spec();
        ProductModel model = sam.model();

        // Hint: Normally the column unique option should do that, but HSQLDB somehow lost it.
        if ( new ProductEao(uuEm).findByPartNo(spec.getPartNo()) != null )
            throw new IllegalArgumentException("PartNo=" + spec.getPartNo() + " exists allready, but create is calles");

        ProductModelEmo productModelEmo = new ProductModelEmo(specEm);
        model = productModelEmo.request(
                model.getFamily().getSeries().getBrand(),
                model.getFamily().getSeries().getGroup(),
                model.getFamily().getSeries().getName(),
                model.getFamily().getName(),
                model.getName());
        spec.setModel(model);
        if ( spec instanceof DisplayAble ) {
            DisplayAble da = (DisplayAble)spec;
            da.setDisplay(new DisplayEmo(specEm).weakRequest(
                    da.getDisplay().getSize(),
                    da.getDisplay().getResolution(),
                    da.getDisplay().getType(),
                    da.getDisplay().getRation()));
        }
        if ( spec instanceof Desktop ) {
            Desktop desktop = (Desktop)spec;
            if ( desktop.getCpu() == null || desktop.getGpu() == null ) throw new IllegalArgumentException("Cpu or Gpu of a Desktop are null. " + desktop);
            Cpu cpu = new CpuEao(specEm).findById(desktop.getCpu().getId());
            Gpu gpu = new GpuEao(specEm).findById(desktop.getGpu().getId());
            if ( cpu != null ) desktop.setCpu(cpu);
            if ( gpu != null ) desktop.setGpu(gpu);
        }
        if ( spec instanceof DesktopBundle ) {
            DesktopBundle bundle = (DesktopBundle)spec;
            if ( bundle.getDesktop().getId() == 0 || bundle.getMonitor().getId() == 0 )
                throw new IllegalArgumentException("Monitor or Desktop are new. Impossible");
            ProductSpecEao specEao = new ProductSpecEao(specEm);
            bundle.setDesktop(specEao.findById(bundle.getDesktop().getId()));
            bundle.setMonitor(specEao.findById(bundle.getMonitor().getId()));
        }

        L.info("create({}) persiting including model change={}", SpecFormater.toDetailedName(spec), (model == spec.getModel()));
        specEm.persist(spec);
        specEm.flush(); // Ensuring Id generation.
        ProductEao productEao = new ProductEao(uuEm);
        Product product = productEao.findByPartNo(spec.getPartNo());
        if ( product == null ) product = new Product();
        product.setGroup(spec.getModel().getFamily().getSeries().getGroup());
        product.setTradeName(spec.getModel().getFamily().getSeries().getBrand());
        product.setPartNo(spec.getPartNo());
        product.setName(spec.getModel().getName());
        product.setDescription(SpecFormater.toSingleLine(spec));
        product.setGtin(sam.gtin());
        if ( !uuEm.contains(product) ) {
            uuEm.persist(product);
            uuEm.flush(); // Ensuring Id generation
        }
        L.debug("create({}) overwriting uniqueunti.Product.id={}", SpecFormater.toDetailedName(spec), product.getId());
        spec.setProductId(product.getId());

        return spec;
    }

    @Override
    public ProductSpec update(SpecAndModel sam) throws IllegalArgumentException {
        Objects.requireNonNull(sam, "Spec and Model must not be null");
        ProductSpec spec = sam.spec();
        ProductModel model = sam.model();

        // 1. Validation
        if ( spec.getProductId() == null ) throw new IllegalArgumentException("ProductSpec has no productId, violation ! " + spec);
        ProductEao productEao = new ProductEao(uuEm);
        Product product = productEao.findById(spec.getProductId());
        if ( product == null ) throw new IllegalArgumentException("ProductSpec.productId=" + spec.getProductId() + " does not have a Product");
        // Allways Update model
        spec.setModel(new ProductModelEao(specEm).findById(model.getId()));
        // 2. + 3. Update Spec
        if ( spec instanceof DisplayAble && ((DisplayAble)spec).getDisplay().getId() == 0 ) {
            DisplayAble monitor = (DisplayAble)spec;
            DisplayEao displayEao = new DisplayEao(specEm);
            Display display = monitor.getDisplay();
            display = displayEao.find(display.getSize(), display.getResolution(), display.getType(), display.getRation());
            if ( display != null ) monitor.setDisplay(display);
        }
        L.info("update({}) merging", SpecFormater.toDetailedName(spec));
        spec = specEm.merge(spec);
        // 4. overwrite Product
        product.setGroup(spec.getModel().getFamily().getSeries().getGroup());
        product.setTradeName(spec.getModel().getFamily().getSeries().getBrand());
        product.setPartNo(spec.getPartNo());
        product.setName(spec.getModel().getName());
        product.setDescription(SpecFormater.toSingleLine(spec));
        product.setGtin(sam.gtin());
        L.debug("update({}) overwriting uniqueunti.Product.id={}", SpecFormater.toDetailedName(spec), product.getId());
        return spec;
    }

    @Override
    public ProductSeries create(TradeName brand, ProductGroup group, String seriesName) throws UserInfoException {
        EntityManager em = specEm;
        // 1. check if there exits a model anytwhere with the same name. -> throw Exception
        ProductSeriesEao seriesEoa = new ProductSeriesEao(em);
        ProductSeries series = seriesEoa.find(seriesName);
        if ( series != null ) throw new UserInfoException("There exits a series " + series + " but we want to create it");
        // 2. Create Family
        series = new ProductSeries(brand, group, seriesName);
        em.persist(series);
        return series;
    }
}
