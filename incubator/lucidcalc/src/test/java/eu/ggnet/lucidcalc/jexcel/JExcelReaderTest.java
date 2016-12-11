/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.lucidcalc.jexcel;

import java.io.File;
import java.util.*;

import org.junit.Test;

import eu.ggnet.lucidcalc.LucidCalcReader;

import static org.junit.Assert.*;

public class JExcelReaderTest {

    public static class SampleBean {

        private String f1;

        private Double f2;

        private Integer f3;

        private Double f4;

        public SampleBean() {
        }

        public SampleBean(String f1, Double f2, Integer f3, Double f4) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
        }

        @Override
        public boolean equals(Object obj) {
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final SampleBean other = (SampleBean)obj;
            if ( (this.f1 == null) ? (other.f1 != null) : !this.f1.equals(other.f1) ) return false;
            if ( this.f2 != other.f2 && (this.f2 == null || !this.f2.equals(other.f2)) ) return false;
            if ( this.f3 != other.f3 && (this.f3 == null || !this.f3.equals(other.f3)) ) return false;
            if ( this.f4 != other.f4 && (this.f4 == null || !this.f4.equals(other.f4)) ) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (this.f1 != null ? this.f1.hashCode() : 0);
            hash = 23 * hash + (this.f2 != null ? this.f2.hashCode() : 0);
            hash = 23 * hash + (this.f3 != null ? this.f3.hashCode() : 0);
            hash = 23 * hash + (this.f4 != null ? this.f4.hashCode() : 0);
            return hash;
        }
    }

    public static final File TEST_DATA = new File("target/test-classes/JExcelReaderTestData.xls");

    /**
     * Test of read method, of class JExcelLucidCalcReader.
     */
    @Test
    public void testReadFile() {
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class).addColumn(1, Double.class).addColumn(2, Integer.class).addColumn(3, Double.class);
        List<List<? extends Object>> expResult = new ArrayList<List<? extends Object>>();
        expResult.add(Arrays.asList("AAA", Double.valueOf(1.3), Integer.valueOf(1), Double.valueOf(10.0)));
        expResult.add(Arrays.asList("BBB", Double.valueOf(1.4), Integer.valueOf(2), Double.valueOf(1.5)));
        expResult.add(Arrays.asList("CCC", Double.valueOf(1.5), Integer.valueOf(3), Double.valueOf(2.49)));
        expResult.add(Arrays.asList("DDD", Double.valueOf(3.55), Integer.valueOf(4), Double.valueOf(55.33)));
        List result = reader.read(TEST_DATA);
        assertEquals(expResult, result);
        assertFalse(reader.isError());
        assertEquals(0, reader.getErrors().size());
    }

    /**
     * Test of read method, of class JExcelLucidCalcReader.
     */
    @Test
    public void testReadFileGenericType() {
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class).addColumn(1, Double.class).addColumn(2, Integer.class).addColumn(3, Double.class);
        List<SampleBean> expResult = new ArrayList<>();
        expResult.add(new SampleBean("AAA", 1.3, 1, 10.0));
        expResult.add(new SampleBean("BBB", 1.4, 2, 1.5));
        expResult.add(new SampleBean("CCC", 1.5, 3, 2.49));
        expResult.add(new SampleBean("DDD", 3.55, 4, 55.33));
        List<SampleBean> result = reader.read(TEST_DATA, new SampleBean());
        assertEquals(expResult, result);
        assertFalse(reader.isError());
        assertEquals(0, reader.getErrors().size());
    }
}
