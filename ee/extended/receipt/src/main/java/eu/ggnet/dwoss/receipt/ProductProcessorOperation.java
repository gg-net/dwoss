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
package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.eao.ProductFamilyEao;
import eu.ggnet.dwoss.spec.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.eao.GpuEao;
import eu.ggnet.dwoss.spec.entity.piece.Display;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.eao.DisplayEao;
import eu.ggnet.dwoss.spec.eao.CpuEao;
import eu.ggnet.dwoss.spec.eao.ProductSeriesEao;
import eu.ggnet.dwoss.spec.entity.DisplayAble;
import eu.ggnet.dwoss.spec.eao.ProductModelEao;
import eu.ggnet.dwoss.spec.entity.ProductSeries;
import eu.ggnet.dwoss.spec.entity.ProductModel;
import eu.ggnet.dwoss.spec.entity.Desktop;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.eao.ProductSpecEao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.format.SpecFormater;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

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

    /**
     * Creates a new ProductSpec and the relating Product and SopoProduct.
     *
     * The process has multiple steps:
     * <ol>
     * <li>Merge the ProductModel and set it in the Spec</li>
     * <li>If Spec is a DisplayAble its assumed the Display is either existent or new.<br />
     * In case it exists, the existent value will be set in the Spec</li>
     * <li>If Spec is a Desktop its assumed that the Cpu and Gpu are existent, and they are merged and set.</li>
     * <li>The Spec is persisted</li>
     * <li>A Product is created and persisted</li>
     * <li>The Spec.productId is set to Product.id (WeakReference)</li>
     * <li>A SopoProduct is searched. If found, it is updated, else a new one is created</li>
     * </ol>
     *
     * @param spec  the spec to persist, must not be null
     * @param model the model for the spec, must not be null or new
     * @throws IllegalArgumentException if Cpu or Gpu in a Desktop are new.
     *
     * @return the detached created ProductSpec
     */
    // TODO: Check if the model as parameter is still needed.
    @Override
    public ProductSpec create(ProductSpec spec, ProductModel model) throws IllegalArgumentException {
        if ( model == null ) throw new NullPointerException("Model is null");
        if ( spec == null ) throw new NullPointerException("ProductSpec is null");
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

        L.info("Persisting {} including model change={}", SpecFormater.toDetailedName(spec), (model == spec.getModel()));
        specEm.persist(spec);

        EntityManager uniqueUnitEm = uuEm;
        ProductEao productEao = new ProductEao(uniqueUnitEm);
        Product product = productEao.findByPartNo(spec.getPartNo());
        if ( product == null ) product = new Product();
        product.setGroup(spec.getModel().getFamily().getSeries().getGroup());
        product.setTradeName(spec.getModel().getFamily().getSeries().getBrand());
        product.setPartNo(spec.getPartNo());
        product.setName(spec.getModel().getName());
        product.setDescription(SpecFormater.toSingleLine(spec));
        L.debug("persisting {}", product);
        if ( !uniqueUnitEm.contains(product) ) uniqueUnitEm.persist(product);
        L.debug("creating weak reference ProductSpec.productId=Product.id value ({})", product.getId());
        spec.setProductId(product.getId());

        return spec;
    }

    /**
     * Return a refreshed ProductSpec with the selected model added.
     *
     * @param spec  the spec to be refreshed.
     * @param model the model to be assosiated, must not be null, but may be equal to spec.getModel
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public ProductSpec refresh(ProductSpec spec, ProductModel model) throws IllegalArgumentException {
        if ( spec.getProductId() == null ) throw new IllegalArgumentException("ProductSpec has no productId, violation ! " + spec);
        if ( spec.getModel().equals(model) ) return spec;
        // 1. Validation
        L.debug("refreshing {}", spec);
        spec = new ProductSpecEao(specEm).findById(spec.getId());
        spec.setModel(new ProductModelEao(specEm).findById(model.getId()));
        return spec;
    }

    /**
     * Updates an existing ProductSpec and the relating Product and SopoProduct.
     *
     * The process has multiple steps:
     * <ol>
     * <li>Validate and throw IllegalArgumentException:
     * <ul>
     * <li>if the ProductSpec.productId == null</li>
     * <li>if there does not exist a Product with ProductSpec.productId</li>
     * <li>if there does not exist a SopoProduct with Product.partNo (unchanged)</li>
     * </ul>
     * </li>
     * <li>If Spec is a DisplayAble its assumed the Display is either existent or new.<br />
     * In case it exists, the existent value will be set in the Spec</li>
     * <li>if the supplied ProductModel is different from ProductSpec.getModel it is merged and set</li>
     * <li>Overwrite all changes in Product</li>
     * <li>Overwrite all changes in SopoProduct</li>
     * <li> If PartNo change propagate to all matching SopoUnits</li>
     * </ol>
     *
     * @param spec the spec to be updated, must not be null and not new
     * @throws IllegalArgumentException if spec.productId == null
     * @return the updated and detached instance of ProductSpec
     */
    @Override
    public ProductSpec update(ProductSpec spec) throws IllegalArgumentException {
        // 1. Validation
        if ( spec.getProductId() == null ) throw new IllegalArgumentException("ProductSpec has no productId, violation ! " + spec);
        ProductEao productEao = new ProductEao(uuEm);
        Product product = productEao.findById(spec.getProductId());
        if ( product == null ) throw new IllegalArgumentException("ProductSpec.productId=" + spec.getProductId() + " does not have a Product");
        // 2. + 3. Update Spec
        if ( spec instanceof DisplayAble && ((DisplayAble)spec).getDisplay().getId() == 0 ) {
            DisplayAble monitor = (DisplayAble)spec;
            DisplayEao displayEao = new DisplayEao(specEm);
            Display display = monitor.getDisplay();
            display = displayEao.find(display.getSize(), display.getResolution(), display.getType(), display.getRation());
            if ( display != null ) monitor.setDisplay(display);
        }
        L.info("updateing {}", SpecFormater.toDetailedName(spec));
        spec = specEm.merge(spec);
        // 4. overwrite Product
        product.setGroup(spec.getModel().getFamily().getSeries().getGroup());
        product.setTradeName(spec.getModel().getFamily().getSeries().getBrand());
        product.setPartNo(spec.getPartNo());
        product.setName(spec.getModel().getName());
        product.setDescription(SpecFormater.toSingleLine(spec));
        L.debug("updateing {}", product);
        return spec;
    }
}
