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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 */
public abstract class CCalcDocument {

    @Valid
    @NotNull
    private final List<CSheet> sheets;

    public CCalcDocument() {
        sheets = new ArrayList<>();
    }

    public CCalcDocument add(CSheet sheet) {
        sheets.add(sheet);
        return this;
    }

    public List<CSheet> getSheets() {
        return sheets;
    }

    public abstract File getFile();

}
