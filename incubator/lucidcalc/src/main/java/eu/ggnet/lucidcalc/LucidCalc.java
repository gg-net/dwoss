/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc;

import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

/**
 * Generator
 */
public abstract class LucidCalc {

    private LucidCalc() {
    }

    public static enum Backend {

        XLS
    }

    public static LucidCalcWriter createWriter(Backend backend) {
        switch (backend) {
            case XLS:
                return new JExcelLucidCalcWriter();
        }
        return null;
    }

    public static LucidCalcReader createReader(Backend backend) {
        switch (backend) {
            case XLS:
                return new JExcelLucidCalcReader();
        }
        return null;
    }
}
