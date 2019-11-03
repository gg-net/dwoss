/*
 * Copyright (C) 2019 GG-Net GmbH
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
package test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class BaseEntityTest {
    
    public static class A extends BaseEntity {
        
        private long id;

        public A(long id) {
            this.id = id;
        }
                
        @Override
        public long getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A{" + "id=" + id + '}';
        }
        
    }
    
    public static class B extends BaseEntity {
        
        private long id;

        public B(long id) {
            this.id = id;
        }
        
        @Override
        public long getId() {
            return id;
        }

        @Override
        public String toString() {
            return "B{" + "id=" + id + '}';
        }
        
    }
    
    @Test
    public void verifyEqualsAndHashCode() {
        
        A a1_id1 = new A(1);
        A a2_id2 = new A(2);
        A a3_id1 = new A(1);
        B b1_id1 = new B(1);
        B b2_id2 = new B(2);
        B b3_id1 = new B(1);
        
        assertThat(a1_id1)
                .isEqualTo(a1_id1)
                .isEqualTo(a3_id1)
                .isNotEqualTo(a2_id2)
                .isNotEqualTo(b1_id1)
                .isNotEqualTo(b2_id2)
                .isNotEqualTo(b3_id1);
        
        Set<BaseEntity> s = new HashSet<>();
        s.add(a1_id1);
        s.add(a2_id2);
        s.add(a3_id1);
        s.add(b1_id1);
        s.add(b2_id2);
        s.add(b3_id1);
        
        assertThat(s).hasSize(4)
                .contains(a1_id1,a2_id2,b1_id1,b2_id2);
        
        
    }
    
}
