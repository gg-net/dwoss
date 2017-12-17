package eu.ggnet.dwoss.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author oliver.guenther
 */
public class MathUtilTest {

    @Test
    public void testRoundedApply() {
        assertEquals(0.95, MathUtil.roundedApply(0.80, 0.19, 0.04), 0.00001);
        assertEquals(0.96, MathUtil.roundedApply(0.81, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.82, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.83, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.84, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.85, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.86, 0.19, 0.04), 0.00001);
        assertEquals(1.04, MathUtil.roundedApply(0.87, 0.19, 0.04), 0.00001);
    }
}
