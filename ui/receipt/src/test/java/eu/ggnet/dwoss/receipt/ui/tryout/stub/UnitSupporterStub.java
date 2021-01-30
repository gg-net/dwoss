/*
 * Copyright (C) 2021 GG-Net GmbH
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

import eu.ggnet.dwoss.receipt.ee.UnitSupporter;

/**
 *
 * @author oliver.guenther
 */
public class UnitSupporterStub implements UnitSupporter {

    @Override
    public boolean isRefurbishIdAvailable(String refurbishId) {
        return true;
    }

    @Override
    public boolean isSerialAvailable(String serial) {
        return true;
    }

    @Override
    public String findRefurbishIdBySerial(String serial) {
        if ( serial.length() < 10 ) return serial;
        return serial.substring(0, 10);
    }

}
