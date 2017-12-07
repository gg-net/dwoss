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
package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.spec.entity.ProductSeries;
import eu.ggnet.dwoss.spec.entity.ProductModel;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.entity.ProductFamily;

import javax.ejb.Remote;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface ProductProcessor {

    /**
     * Creates a new Cpu instance.
     *
     * @param cpu the Cpu to create
     * @return the Cpu instance from databes, but detached.
     * @throws IllegalArgumentException if there exist a cpu with the same series and the name.
     */
    Cpu create(Cpu cpu) throws IllegalArgumentException;

    /**
     * Creates a new Gpu instance.
     *
     * @param gpu the Gpu to create
     * @return the Gpu instance from databes, but detached.
     * @throws IllegalArgumentException if there exist a Gpu with the same series and the name.
     */
    Gpu create(Gpu gpu) throws IllegalArgumentException;

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
    ProductModel create(final TradeName brand, final ProductGroup group, ProductSeries series, ProductFamily family, final String modelName);

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
    ProductFamily create(final TradeName brand, final ProductGroup group, ProductSeries series, final String familyName);

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
     * @param gtin the value of gtin
     * @throws IllegalArgumentException if Cpu or Gpu in a Desktop are new.
     *
     * @return the eu.ggnet.dwoss.spec.entity.ProductSpec
     */
    // TODO: Check if the model as parameter is still needed.
    ProductSpec create(ProductSpec spec, ProductModel model, long gtin) throws IllegalArgumentException;

    /**
     * Return a refreshed ProductSpec with the selected model added.
     *
     * @param spec  the spec to be refreshed.
     * @param model the model to be assosiated, must not be null, but may be equal to spec.getModel
     * @return
     * @throws IllegalArgumentException
     */
    ProductSpec refresh(ProductSpec spec, ProductModel model) throws IllegalArgumentException;

    /**
     * Updates an existing detached Cpu.
     *
     * @param cpu the detached Cpu
     * @return the reatached updated Cpu
     * @throws IllegalArgumentException if there allready exists a Cpu with the same series and name
     */
    Cpu update(Cpu cpu) throws IllegalArgumentException;

    /**
     * Updates an existing detached Gpu.
     *
     * @param gpu the detached Gpu
     * @return the reatached updated Gpu
     * @throws IllegalArgumentException if there allready exists a Gpu with the same series and name
     */
    Gpu update(Gpu gpu) throws IllegalArgumentException;

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
     * @param spec  the spec to be updated, must not be null
     * @param gtin the value of gtin
     * @throws IllegalArgumentException if spec.productId == null
     * @return the eu.ggnet.dwoss.spec.entity.ProductSpec
     */
    ProductSpec update(ProductSpec spec, long gtin) throws IllegalArgumentException;
}
