/*
 * Copyright (C) 2024 GG-Net GmbH
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.stock.api.*;

/**
 *
 * @author oliver.guenther
 */
public class StockApiStub implements StockApi {

    @Override
    public List<SimpleShipment> findShipmentsSince(LocalDate since) {
        return List.of(new StockApi.SimpleShipment(1, "Shipment 1"),new StockApi.SimpleShipment(2, "Shipment 2"));
    }

    @Override
    public List<PicoStock> findAllStocks() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SimpleStockUnit findByRefurbishId(String refurbishId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Map<String, SimpleStockUnit> findByRefurbishIds(List<String> refurbishIds) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Scraped> scrap(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Scraped> delete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void perpareTransferByUniqueUnitIds(List<Long> uniqueUnitIds, int destinationStockId, String arranger, String comment) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
