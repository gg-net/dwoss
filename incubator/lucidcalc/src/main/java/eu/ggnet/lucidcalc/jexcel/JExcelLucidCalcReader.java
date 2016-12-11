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
package eu.ggnet.lucidcalc.jexcel;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.lucidcalc.LucidCalcReader;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.read.biff.BlankCell;

/**
 * The most KIS tool for reading plain calc docs.
 * If not other configured, the defaults are
 * <ul>
 * <li>headline = true</li>
 * <li>trim = true</li>
 * <li>columns = {0}</li>
 * <li>all Types are String</li>
 * </ul>
 *
 * @author oliver.guenther
 */
// TODO: Unittest
public class JExcelLucidCalcReader implements LucidCalcReader {

    private final static Logger L = LoggerFactory.getLogger(JExcelLucidCalcReader.class);

    /**
     * Indicator to overjump the first line
     */
    private boolean headline;

    /**
     * Indicator to trim all strings on read.
     */
    private boolean trim;

    /**
     * the columns to be read
     * (e.g. {0,1,5} => resultlist has 3 elements 1 => column 0, 2 => column 1, 3 => column 5)
     */
    private Map<Integer, Class<? extends Object>> columns;

    private List<String> errors;

    /**
     * Default constructor, sets, headline and trim = true.
     */
    public JExcelLucidCalcReader() {
        headline = true;
        trim = true;
        columns = new HashMap<>();
        errors = new ArrayList<>();
    }

    /**
     * Add a new Column definition for the read.
     * TODO: You may not add a column with a lower number than all columns before, check or change
     *
     * @param id   the column id (0=A, 1=B, 2=C, ... )
     * @param type the Type of the column. Allowed Values: String,Double,Integer
     * @return the JExcelLucidCalcReader
     */
    @Override
    public JExcelLucidCalcReader addColumn(int id, Class<? extends Object> type) {
        this.columns.put(id, type);
        return this;
    }

    /**
     * Add new Column definition for the read, all types are String
     *
     * @param columns the columns
     */
    @Override
    public void setColumns(int... columns) {
        for (int i : columns) {
            this.columns.put(i, String.class);
        }
    }

    @Override
    public boolean isHeadline() {
        return headline;
    }

    @Override
    public void setHeadline(boolean headline) {
        this.headline = headline;
    }

    @Override
    public boolean isTrim() {
        return trim;
    }

    @Override
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    @Override
    public boolean isError() {
        return !errors.isEmpty();
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Reads an xls file and returns the contests as List of Instances of Type U
     * TODO: Inference Mechanism only counts the Parameters, this can be done better and more secure.
     *
     * @param <U>   the type to fill withe content
     * @param file  the file to read
     * @param clazz the clazz to build the container Instance.
     * @return the list
     */
    @Override
    public <U> List<U> read(File file, Class<U> clazz) {
        return read(toWorkbook(file), clazz);
    }

    @Override
    public <U> List<U> read(InputStream is, Class<U> clazz) {
        return read(toWorkbook(is), clazz);
    }

    private <U> List<U> read(Workbook doc, Class<U> clazz) {
        Constructor constructor = null;
        for (Constructor c : clazz.getConstructors()) {
            if ( c.getParameterTypes().length == columns.size() ) {
                constructor = c;
                break;
            }
        }
        if ( constructor == null ) throw new RuntimeException(clazz + " doesnt have a constructor with " + columns.size() + " Parameters");
        List<U> result = new ArrayList<>();
        for (List<? extends Object> line : read(doc)) {
            try {
                L.debug("Creating Instance of {} with {}", clazz, line);
                U u = clazz.cast(constructor.newInstance(line.toArray()));
                result.add(u);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                List<Class<?>> clazzes = new ArrayList<>();
                for (Object object : line) {
                    clazzes.add(object == null ? null : object.getClass());
                }
                throw new RuntimeException("Could not create an Instance of " + clazz
                        + "\nParamters(size=" + line.size() + "):" + line
                        + "\nClasses(size=" + clazzes.size() + "):" + clazzes
                        + "\nConstructorTypes(size=" + constructor.getParameterTypes().length + "):" + Arrays.toString(constructor.getParameterTypes()), ex);
            }
        }
        return result;
    }

    /**
     * Reads an xls file and returns the contests as List of Instances of Type U
     *
     * TODO: Inference Mechanism only counts the Parameters, this can be done better and more secure.
     *
     *
     * @param <U>      the type
     * @param file     the file to be read
     * @param instance
     * @return the list
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U> List<U> read(File file, U instance) {
        return (List<U>)read(file, instance.getClass());
    }

    @Override
    public List<List<? extends Object>> read(File file) {
        return read(toWorkbook(file));
    }

    private Workbook toWorkbook(File file) {
        try {
            return Workbook.getWorkbook(file);
        } catch (IOException | BiffException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Workbook toWorkbook(InputStream is) {
        try {
            return Workbook.getWorkbook(is);
        } catch (IOException | BiffException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Reads an xls file and returns the contents as list of lists.
     *
     * @param file the file to be read
     * @return the list
     */
    // TODO: Check null file
    private List<List<? extends Object>> read(Workbook doc) {
        if ( columns.isEmpty() ) columns.put(0, String.class); // If no columns are defined, just read in the first as String

        List<List<? extends Object>> result = new ArrayList<>();
        Sheet sheet = doc.getSheet(0);
        for (int rowIndex = (headline ? 1 : 0); rowIndex < sheet.getRows(); rowIndex++) {
            if ( isEmptyRow(sheet, rowIndex) ) continue; // Ignore empty rows
            List<Object> line = new ArrayList<>();
            result.add(line);
            for (Integer columnIndex : new TreeSet<>(columns.keySet())) {
                Cell cell = sheet.getCell(columnIndex, rowIndex);
                L.debug("Reading Cell (c=" + columnIndex + ",r=" + rowIndex + ",type=" + cell.getClass().getSimpleName() + ")=" + cell.getContents());
                Class<? extends Object> type = columns.get(columnIndex);
                if ( cell instanceof BlankCell ) {
                    line.add(null);
                } else if ( type.equals(String.class) ) {
                    String value = cell.getContents();
                    if ( trim && value != null ) value = value.trim();
                    line.add(value);
                } else if ( type.equals(Double.class) ) {
                    try {
                        Double value = ((NumberCell)cell).getValue();
                        line.add(value);
                    } catch (ClassCastException cce) {
                        errors.add("Error(row=" + rowIndex + ",column=" + columnIndex + ",value=" + cell.getContents() + ") Type is not Double, ErrorMessage(" + cce.getMessage() + ")");
                        line.add(-9999999.99);
                    }
                } else if ( type.equals(Integer.class) ) {
                    try {
                        Double value = ((NumberCell)cell).getValue();
                        line.add(value.intValue());
                    } catch (ClassCastException cce) {
                        errors.add("Error(row=" + rowIndex + ",column=" + columnIndex + ",value=" + cell.getContents() + ") Type is not Integer, ErrorMessage(" + cce.getMessage() + ")");
                        line.add(-9999999);
                    }
                } else if ( type.equals(Date.class) ) {
                    try {
                        if ( cell.getContents() == null || cell.getContents().isEmpty() ) line.add(null);
                        else {
                            Date value = ((DateCell)cell).getDate();
                            line.add(value);
                        }
                    } catch (ClassCastException cce) {
                        errors.add("Error(row=" + rowIndex + ",column=" + columnIndex + ",value=" + cell.getContents() + ") Type is not Date, ErrorMessage(" + cce.getMessage() + ")");
                        line.add(-9999999);
                    }
                } else {
                    throw new IllegalArgumentException(type + " not implemented");
                }
            }
        }
        doc.close();
        return result;
    }

    private boolean isEmptyRow(Sheet sheet, int i) {
        for (Integer column : columns.keySet()) {
            Cell c = sheet.getCell(column, i);
            if ( c instanceof BlankCell ) continue;
            if ( c.getContents() == null ) continue;
            if ( c.getContents().trim().equals("") ) continue;
            return false;
        }
        return true;
    }
}
