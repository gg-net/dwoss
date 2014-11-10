package eu.ggnet.dwoss.util;



import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class MathUtilTest {

    @Test
    public void testRoundedApply() {
        assertEquals(1, MathUtil.roundedApply(0.82, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.83, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.84, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.85, 0.19, 0.04), 0.00001);
        assertEquals(1, MathUtil.roundedApply(0.86, 0.19, 0.04), 0.00001);
    }
}
