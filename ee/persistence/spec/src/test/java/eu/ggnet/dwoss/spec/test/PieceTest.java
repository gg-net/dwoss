package eu.ggnet.dwoss.spec.test;

import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import java.util.HashSet;
import java.util.Set;

import javax.validation.*;

import org.junit.*;

import static org.junit.Assert.*;

//TODO two identic GPUs should not exist.
/**
 *
 * @author bastian.venz
 */
public class PieceTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Test of getExtras method, of class BasicSpec.
     */
    @Test
    public void testGpu() {
        Gpu gpu = new Gpu();
        Set<ConstraintViolation<Gpu>> violations = validator.validate(gpu);
        assertEquals(2, violations.size());
        Set<String> properties = new HashSet<>();
        for (ConstraintViolation<Gpu> constraintViolation : violations) {
            properties.add(constraintViolation.getPropertyPath().toString());
        }
        assertTrue(properties.contains("series"));
        assertTrue(properties.contains("model"));

        gpu.setSeries(Gpu.Series.GEFORCE_8000);

        violations = validator.validate(gpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        gpu.setModel("");

        violations = validator.validate(gpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        gpu.setModel("  ");

        violations = validator.validate(gpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        gpu.setModel("Hd334");
        violations = validator.validate(gpu);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testGpuSeries() {
        for (Gpu.Series series : Gpu.Series.values()) {
            assertNotNull(series.getManufacturer());
        }
    }

    @Test
    public void testCpuSeries() {
        for (Cpu.Series series : Cpu.Series.values()) {
            assertNotNull(series.getManufacturer());
        }
    }

    @Test
    public void testCpu() {
        Cpu cpu = new Cpu();
        Set<ConstraintViolation<Cpu>> violations = validator.validate(cpu);
        assertEquals(2, violations.size());
        Set<String> properties = new HashSet<>();
        for (ConstraintViolation<Cpu> constraintViolation : violations) {
            properties.add(constraintViolation.getPropertyPath().toString());
        }
        assertTrue(properties.contains("series"));
        assertTrue(properties.contains("model"));

        cpu.setSeries(Cpu.Series.AMD_V);

        violations = validator.validate(cpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        cpu.setModel("");

        violations = validator.validate(cpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        cpu.setModel("  ");

        violations = validator.validate(cpu);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().equals("model"));

        cpu.setModel("i3-324");
        violations = validator.validate(cpu);
        assertTrue(violations.isEmpty());
    }
}
