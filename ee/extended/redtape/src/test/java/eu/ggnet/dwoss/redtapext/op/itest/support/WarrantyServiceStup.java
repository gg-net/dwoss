/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.op.itest.support;

import eu.ggnet.dwoss.mandator.api.service.WarrantyService;
import eu.ggnet.dwoss.common.api.values.TradeName;

import static eu.ggnet.dwoss.common.api.values.TradeName.ONESELF;

/**
 *
 * @author oliver
 */
public class WarrantyServiceStup implements WarrantyService {

    public static final String WARRANTY_PART_NO = "DEH2381234";

    @Override
    public boolean isWarranty(String partNo) {
        return WARRANTY_PART_NO.equals(partNo);
    }

    @Override
    public TradeName warrantyContractor(String partNo) {
        return ONESELF;
    }

};
