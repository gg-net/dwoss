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

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.*;
import eu.ggnet.dwoss.spec.ee.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.ShopCategory;

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
    
    @Inject
    private ProductFamilyEao pfEao;
    
    @Inject
    private ProductModelEao pmEao;

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
     *
     * @param familyId  must be an id of an existing family
     * @param modelName the name of the model
     *
     * @return the persisted and detached Model
     * @throws UserInfoException if the series with seriesid or the family with familyId does not exist or the modelName is already present.
     */
    @Override
    public ProductModel createModel(long familyId, final String modelName) throws UserInfoException {
        EntityManager em = specEm;

        if ( pmEao.find(modelName) != null ) throw new UserInfoException("ProductModel " + modelName + " existiert schon");
        ProductFamily family = pfEao.findById(familyId);
        if (family == null) throw new UserInfoException("ProductFamily mit id=" + familyId + " existiert nicht");

        ProductModel model = new ProductModel(modelName);
        model.setFamily(family);
        em.persist(model);
        return model;
    }

    /**
     * Creates and Persists a ProductFamily.
     *
     * @param seriesId   the seriesId
     * @param familyName the familyName
     *
     * @return the persisted and detached ProductFamily
     */
    @Override
    public ProductFamily createFamily(long seriesId, final String familyName) throws UserInfoException {
        EntityManager em = specEm;
        // 1. check if there exits a model anytwhere with the same name. -> throw Exception
        ProductFamilyEao familyEao = new ProductFamilyEao(em);
        if ( familyEao.find(familyName) != null ) throw new UserInfoException("Die ProductFamily:" + familyName + " existiert schon");
        ProductSeriesEao seriesEao = new ProductSeriesEao(em);
        ProductSeries series = seriesEao.findById(seriesId);
        if ( series == null ) throw new UserInfoException("ProductSeries mit id=" + seriesId + " existiert nicht");
        ProductFamily family = new ProductFamily(familyName);
        family.setSeries(series);
        em.persist(family);
        return family;
    }

    @Override
    public ProductSpec create(SpecAndModel sam) throws IllegalArgumentException {
        Objects.requireNonNull(sam, "Spec and Model must not be null");
        ProductSpec spec = sam.spec();

        // Hint: Normally the column unique option should do that, but HSQLDB somehow lost it.
        if ( new ProductEao(uuEm).findByPartNo(spec.getPartNo()) != null )
            throw new IllegalArgumentException("PartNo=" + spec.getPartNo() + " exists allready, but create is called");

        ProductModel model = specEm.find(ProductModel.class, sam.modelId());
        if ( model == null )
            throw new IllegalStateException("No spec.ProductModel with Id=" + sam.modelId() + ", should be impossible");

        spec.setModel(model);
        if ( spec instanceof DisplayAble da ) {
            da.setDisplay(new DisplayEmo(specEm).weakRequest(
                    da.getDisplay().getSize(),
                    da.getDisplay().getResolution(),
                    da.getDisplay().getType(),
                    da.getDisplay().getRation()));
        }
        if ( spec instanceof Desktop desktop ) {
            if ( desktop.getCpu() == null || desktop.getGpu() == null ) throw new IllegalArgumentException("Cpu or Gpu of a Desktop are null. " + desktop);
            Cpu cpu = new CpuEao(specEm).findById(desktop.getCpu().getId());
            Gpu gpu = new GpuEao(specEm).findById(desktop.getGpu().getId());
            if ( cpu != null ) desktop.setCpu(cpu);
            if ( gpu != null ) desktop.setGpu(gpu);
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
        product.setRch(sam.rch());

        if ( sam.nullableShopCategory() != null ) {
            // If the ShopCategory is selected in the ui it exists in the database.
            product.setShopCategory(uuEm.find(ShopCategory.class, sam.nullableShopCategory().id()));
        }

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

        // 1. Validation
        if ( spec.getProductId() == null ) throw new IllegalArgumentException("ProductSpec has no productId, violation ! " + spec);
        ProductEao productEao = new ProductEao(uuEm);
        Product product = productEao.findById(spec.getProductId());
        if ( product == null ) throw new IllegalArgumentException("ProductSpec.productId=" + spec.getProductId() + " does not have a Product");
        // Allways Update model
        spec.setModel(new ProductModelEao(specEm).findById(sam.modelId()));
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
        product.setRch(sam.rch());

        if ( sam.nullableShopCategory() != null ) {
            // If the ShopCategory is selected in the ui it exists in the database.
            product.setShopCategory(uuEm.find(ShopCategory.class, sam.nullableShopCategory().id()));
        } else {
            product.setShopCategory(null);
        }

        L.debug("update({}) overwriting uniqueunti.Product.id={}", SpecFormater.toDetailedName(spec), product.getId());
        return spec;
    }

    @Override
    public ProductSeries createSeries(TradeName brand, ProductGroup group, String seriesName) throws UserInfoException {
        EntityManager em = specEm;
        // 1. check if there exits a model anytwhere with the same name. -> throw Exception
        ProductSeriesEao seriesEoa = new ProductSeriesEao(em);
        ProductSeries series = seriesEoa.find(seriesName);
        if ( series != null ) throw new UserInfoException("Serie " + series + " existiert schon");
        // 2. Create Family
        series = new ProductSeries(brand, group, seriesName);
        em.persist(series);
        return series;
    }
}
