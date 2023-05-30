/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ee.entity;

import java.time.LocalDate;

import javax.persistence.*;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

/**
 *
 * @author oliver.guenther
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class StockCount extends BaseEntity {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    private LocalDate created;

    private int shipmentsAnnounced;

    private int shipmentsAnnouncedUnits;

    private int shipmentsDelivered;

    private int shipmentsDeliveredUnits;

    private int shipmentsOpened;

    private int shipmentsOpenedUnits;

    private int shipmentsOpenedRemainderUnits;

    private int stockUnitsAvailable;

    private int stockUnitsAvailablePriceZero;

    private int stockUnitsAvailablePriceBelowOneHundred;

    private int stockUnitsAvailablePriceBelowThreeHundred;

    private int stockUnitsAvailablePriceAboveThreeHundred;

    private int stockUnitsInTransfer;

    private int stockUnitsInTransferPriceZero;

    private int stockUnitsInTransferPriceBelowOneHundred;

    private int stockUnitsInTransferPriceBelowThreeHundred;

    private int stockUnitsInTransferPriceAboveThreeHundred;

    public StockCount() {
    }

    /**
     * Creates Entity form Api.
     * 
     * @param api the api instance.
     * @return the entity.
     */
    public static StockCount fromApi(eu.ggnet.dwoss.report.api.StockCount api) {
        StockCount sc = new StockCount();
        sc.setCreated(api.created());
        sc.setShipmentsAnnounced(api.shipmentsAnnounced());
        sc.setShipmentsAnnouncedUnits(api.shipmentsAnnouncedUnits());
        sc.setShipmentsDelivered(api.shipmentsDelivered());
        sc.setShipmentsDeliveredUnits(api.shipmentsDeliveredUnits());
        sc.setShipmentsOpened(api.shipmentsOpened());
        sc.setShipmentsOpenedUnits(api.shipmentsOpenedUnits());
        sc.setShipmentsOpenedRemainderUnits(api.shipmentsOpenedRemainderUnits());
        sc.setStockUnitsAvailable(api.stockUnitsAvailable());
        sc.setStockUnitsAvailablePriceZero(api.stockUnitsAvailablePriceZero());
        sc.setStockUnitsAvailablePriceBelowOneHundred(api.stockUnitsAvailablePriceBelowOneHundred());
        sc.setStockUnitsAvailablePriceBelowThreeHundred(api.stockUnitsAvailablePriceBelowThreeHundred());
        sc.setStockUnitsAvailablePriceAboveThreeHundred(api.stockUnitsAvailablePriceAboveThreeHundred());
        sc.setStockUnitsInTransfer(api.stockUnitsInTransfer());
        sc.setStockUnitsInTransferPriceZero(api.stockUnitsInTransferPriceZero());
        sc.setStockUnitsInTransferPriceBelowOneHundred(api.stockUnitsInTransferPriceBelowOneHundred());
        sc.setStockUnitsInTransferPriceBelowThreeHundred(api.stockUnitsInTransferPriceBelowThreeHundred());
        sc.setStockUnitsInTransferPriceAboveThreeHundred(api.stockUnitsInTransferPriceAboveThreeHundred());
        return sc;
    }

    /**
     * Returns an api instance of the entiy.
     * 
     * @return an api instance of the entiy. 
     */
    public eu.ggnet.dwoss.report.api.StockCount toApi() {
        return new eu.ggnet.dwoss.report.api.StockCount.Builder()
                .created(getCreated())
                .shipmentsAnnounced(getShipmentsAnnounced())
                .shipmentsAnnouncedUnits(getShipmentsAnnouncedUnits())
                .shipmentsDelivered(getShipmentsDelivered())
                .shipmentsDeliveredUnits(getShipmentsDeliveredUnits())
                .shipmentsOpened(getShipmentsOpened())
                .shipmentsOpenedUnits(getShipmentsOpenedUnits())
                .shipmentsOpenedRemainderUnits(getShipmentsOpenedRemainderUnits())
                .stockUnitsAvailable(getStockUnitsAvailable())
                .stockUnitsAvailablePriceZero(getStockUnitsAvailablePriceZero())
                .stockUnitsAvailablePriceBelowOneHundred(getStockUnitsAvailablePriceBelowOneHundred())
                .stockUnitsAvailablePriceBelowThreeHundred(getStockUnitsAvailablePriceBelowThreeHundred())
                .stockUnitsAvailablePriceAboveThreeHundred(getStockUnitsAvailablePriceAboveThreeHundred())
                .stockUnitsInTransfer(getStockUnitsInTransfer())
                .stockUnitsInTransferPriceZero(getStockUnitsInTransferPriceZero())
                .stockUnitsInTransferPriceBelowOneHundred(getStockUnitsInTransferPriceBelowOneHundred())
                .stockUnitsInTransferPriceBelowThreeHundred(getStockUnitsInTransferPriceBelowThreeHundred())
                .stockUnitsInTransferPriceAboveThreeHundred(getStockUnitsInTransferPriceAboveThreeHundred())
                .build();
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getOptLock() {
        return optLock;
    }

    public void setOptLock(short optLock) {
        this.optLock = optLock;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public int getShipmentsAnnounced() {
        return shipmentsAnnounced;
    }

    public void setShipmentsAnnounced(int shipmentsAnnounced) {
        this.shipmentsAnnounced = shipmentsAnnounced;
    }

    public int getShipmentsAnnouncedUnits() {
        return shipmentsAnnouncedUnits;
    }

    public void setShipmentsAnnouncedUnits(int shipmentsAnnouncedUnits) {
        this.shipmentsAnnouncedUnits = shipmentsAnnouncedUnits;
    }

    public int getShipmentsDelivered() {
        return shipmentsDelivered;
    }

    public void setShipmentsDelivered(int shipmentsDelivered) {
        this.shipmentsDelivered = shipmentsDelivered;
    }

    public int getShipmentsDeliveredUnits() {
        return shipmentsDeliveredUnits;
    }

    public void setShipmentsDeliveredUnits(int shipmentsDeliveredUnits) {
        this.shipmentsDeliveredUnits = shipmentsDeliveredUnits;
    }

    public int getShipmentsOpened() {
        return shipmentsOpened;
    }

    public void setShipmentsOpened(int shipmentsOpened) {
        this.shipmentsOpened = shipmentsOpened;
    }

    public int getShipmentsOpenedUnits() {
        return shipmentsOpenedUnits;
    }

    public void setShipmentsOpenedUnits(int shipmentsOpenedUnits) {
        this.shipmentsOpenedUnits = shipmentsOpenedUnits;
    }

    public int getShipmentsOpenedRemainderUnits() {
        return shipmentsOpenedRemainderUnits;
    }

    public void setShipmentsOpenedRemainderUnits(int shipmentsOpenedRemainderUnits) {
        this.shipmentsOpenedRemainderUnits = shipmentsOpenedRemainderUnits;
    }

    public int getStockUnitsAvailable() {
        return stockUnitsAvailable;
    }

    public void setStockUnitsAvailable(int stockUnitsAvailable) {
        this.stockUnitsAvailable = stockUnitsAvailable;
    }

    public int getStockUnitsAvailablePriceZero() {
        return stockUnitsAvailablePriceZero;
    }

    public void setStockUnitsAvailablePriceZero(int stockUnitsAvailablePriceZero) {
        this.stockUnitsAvailablePriceZero = stockUnitsAvailablePriceZero;
    }

    public int getStockUnitsAvailablePriceBelowOneHundred() {
        return stockUnitsAvailablePriceBelowOneHundred;
    }

    public void setStockUnitsAvailablePriceBelowOneHundred(int stockUnitsAvailablePriceBelowOneHundred) {
        this.stockUnitsAvailablePriceBelowOneHundred = stockUnitsAvailablePriceBelowOneHundred;
    }

    public int getStockUnitsAvailablePriceBelowThreeHundred() {
        return stockUnitsAvailablePriceBelowThreeHundred;
    }

    public void setStockUnitsAvailablePriceBelowThreeHundred(int stockUnitsAvailablePriceBelowThreeHundred) {
        this.stockUnitsAvailablePriceBelowThreeHundred = stockUnitsAvailablePriceBelowThreeHundred;
    }

    public int getStockUnitsAvailablePriceAboveThreeHundred() {
        return stockUnitsAvailablePriceAboveThreeHundred;
    }

    public void setStockUnitsAvailablePriceAboveThreeHundred(int stockUnitsAvailablePriceAboveThreeHundred) {
        this.stockUnitsAvailablePriceAboveThreeHundred = stockUnitsAvailablePriceAboveThreeHundred;
    }

    public int getStockUnitsInTransfer() {
        return stockUnitsInTransfer;
    }

    public void setStockUnitsInTransfer(int stockUnitsInTransfer) {
        this.stockUnitsInTransfer = stockUnitsInTransfer;
    }

    public int getStockUnitsInTransferPriceZero() {
        return stockUnitsInTransferPriceZero;
    }

    public void setStockUnitsInTransferPriceZero(int stockUnitsInTransferPriceZero) {
        this.stockUnitsInTransferPriceZero = stockUnitsInTransferPriceZero;
    }

    public int getStockUnitsInTransferPriceBelowOneHundred() {
        return stockUnitsInTransferPriceBelowOneHundred;
    }

    public void setStockUnitsInTransferPriceBelowOneHundred(int stockUnitsInTransferPriceBelowOneHundred) {
        this.stockUnitsInTransferPriceBelowOneHundred = stockUnitsInTransferPriceBelowOneHundred;
    }

    public int getStockUnitsInTransferPriceBelowThreeHundred() {
        return stockUnitsInTransferPriceBelowThreeHundred;
    }

    public void setStockUnitsInTransferPriceBelowThreeHundred(int stockUnitsInTransferPriceBelowThreeHundred) {
        this.stockUnitsInTransferPriceBelowThreeHundred = stockUnitsInTransferPriceBelowThreeHundred;
    }

    public int getStockUnitsInTransferPriceAboveThreeHundred() {
        return stockUnitsInTransferPriceAboveThreeHundred;
    }

    public void setStockUnitsInTransferPriceAboveThreeHundred(int stockUnitsInTransferPriceAboveThreeHundred) {
        this.stockUnitsInTransferPriceAboveThreeHundred = stockUnitsInTransferPriceAboveThreeHundred;
    }

}
