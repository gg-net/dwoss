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
import java.io.InputStream;
import java.util.List;

import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

/**
 *
 * @author oliver.guenther
 */
public interface LucidCalcReader {

    /**
     * Add a new Column definition for the read.
     * TODO: You may not add a column with a lower number than all columns before, check or change
     *
     * @param id   the column id (0=A, 1=B, 2=C, ... )
     * @param type the Type of the column. Allowed Values: String,Double,Integer
     * @return the JExcelLucidCalcReader
     */
    JExcelLucidCalcReader addColumn(int id, Class<? extends Object> type);

    List<String> getErrors();

    boolean isError();

    boolean isHeadline();

    boolean isTrim();

    /**
     * Reads an xls file and returns the contests as List of Instances of Type U
     * <p>
     * TODO: Inference Mechanism only counts the Parameters, this can be done better and more secure.
     *
     *
     * @param <U>   the type
     * @param file  the file to be read
     * @param clazz the clazz to build the container Instance.
     * @return the list
     */
    <U> List<U> read(File file, Class<U> clazz);

    <U> List<U> read(InputStream is, Class<U> clazz);

    /**
     * Reads an xls file and returns the contests as List of Instances of Type U
     * <p>
     * TODO: Inference Mechanism only counts the Parameters, this can be done better and more secure.
     *
     *
     * @param <U>      the type
     * @param file     the file to be read
     * @param instance the instance to infer the type
     * @return the list
     */
    <U> List<U> read(File file, U instance);

    List<List<? extends Object>> read(File file);

    /**
     * Add new Column definition for the read, all types are String
     *
     * @param columns the columns
     */
    void setColumns(int... columns);

    void setHeadline(boolean headline);

    void setTrim(boolean trim);
}
