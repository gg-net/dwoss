/*
 * Copyright (C) 2022 GG-Net GmbH
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

import eu.ggnet.dwoss.core.common.values.OttoRules;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 *
 * @author oliver.guenther
 */
public class OttoRulesTest {
    
    @Test
    public void validPartNos() {
        Assertions.assertThat(OttoRules.validatePartNo("123456")).isNull();  
        Assertions.assertThat(OttoRules.validatePartNo("12345678")).isNull();  
        Assertions.assertThat(OttoRules.validatePartNo("12345")).isNotNull();  
        Assertions.assertThat(OttoRules.validatePartNo("1234567")).isNotNull();  
        Assertions.assertThat(OttoRules.validatePartNo("123456789")).isNotNull();  
        Assertions.assertThat(OttoRules.validatePartNo("123456  ")).isNotNull();  
        Assertions.assertThat(OttoRules.validatePartNo("  12345")).isNotNull();  
        Assertions.assertThat(OttoRules.validatePartNo("12  345")).isNotNull();  
    }
}
