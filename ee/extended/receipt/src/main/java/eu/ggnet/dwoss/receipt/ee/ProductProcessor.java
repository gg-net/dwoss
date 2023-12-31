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

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import jakarta.ejb.Remote;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.uniqueunit.api.ShopCategory;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface ProductProcessor {

    public class SpecAndModel implements Serializable {

        private final ProductSpec spec;

        private final ProductModel model;

        private final long gtin;
        
        private final ShopCategory shopCategory;
        
        private final boolean rch;

        public SpecAndModel(ProductSpec spec, ProductModel model, long gtin, ShopCategory shopCategory, boolean rch) {
            this.spec = Objects.requireNonNull(spec, "spec must not be null");
            this.model = Objects.requireNonNull(model, "model must not be null");
            this.shopCategory = shopCategory; // May be null;
            this.gtin = gtin;
            this.rch = rch;
        }

        public ProductSpec spec() {
            return spec;
        }

        public ProductModel model() {
            return model;
        }

        public long gtin() {
            return gtin;
        }
        
        public ShopCategory nullableShopCategory() {
            return shopCategory;
        }

        public boolean rch() {
            return rch;
        }        

        @Override
        public String toString() {
            return "SpecAndModel{" + "spec=" + spec + ", model=" + model + ", gtin=" + gtin + ", shopCategory=" + shopCategory + ", rch=" + rch + '}';
        }

    }

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
     * Creates and persists a ProductSeries.
     *
     * @param brand      the brand
     * @param group      the group
     * @param seriesName the series.
     * @return the persisted and detache entity
     * @throws UserInfoException if the supplied data is not correct.
     */
    ProductSeries create(final TradeName brand, final ProductGroup group, final String seriesName) throws UserInfoException;

    /**
     * Creates a new ProductSpec and the relating Product and SopoProduct.
     * <p>
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
     * @param sam the spec and model
     * @throws IllegalArgumentException if Cpu or Gpu in a Desktop are new.
     *
     * @return the eu.ggnet.dwoss.spec.entity.ProductSpec
     */
    // TODO: The methods create,update an refresh can be merged into a createOrUpdate, moved to the SpecAgent. The Product change can be realised via an event.
    ProductSpec create(SpecAndModel sam) throws IllegalArgumentException;

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
     * <p>
     * The process has multiple steps:
     * <ol>
     * <li>Validate and throw IllegalArgumentException:
     * <ul>
     * <li>if the ProductSpec.productId == null
     * <li>if there does not exist a Product with ProductSpec.productId
     * <li>if there does not exist a SopoProduct with Product.partNo (unchanged)
     * </ul>
     * <li>if the supplied model differes from the model of the spec, it is updated on the spec
     * <li>If Spec is a DisplayAble its assumed the Display is either existent or new. In case it exists, the existent value will be set in the Spec
     * <li>if the supplied ProductModel is different from ProductSpec.getModel it is merged and set
     * <li>Overwrite all changes in Product
     * <li>Overwrite all changes in SopoProduct
     * <li>If PartNo change propagate to all matching SopoUnits
     * </ol>
     *
     * @param sam the spec and model
     * @throws IllegalArgumentException if spec.productId == null
     * @return the eu.ggnet.dwoss.spec.entity.ProductSpec
     */
    ProductSpec update(SpecAndModel sam) throws IllegalArgumentException;
}
