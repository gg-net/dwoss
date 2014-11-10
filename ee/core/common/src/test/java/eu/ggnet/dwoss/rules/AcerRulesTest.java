package eu.ggnet.dwoss.rules;

import eu.ggnet.dwoss.rules.AcerRules;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcerRulesTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testValidatePartNo() {
        assertNull(AcerRules.validatePartNo("LX.AAAA0.001"));
        assertNull(AcerRules.validatePartNo("LX.AAAA0.A01"));
        assertNull(AcerRules.validatePartNo("LU.AWWA0.0X1"));
        assertNotNull(AcerRules.validatePartNo("LU.AWWA0.0_1"));
        assertNotNull(AcerRules.validatePartNo("LU.AaWA0.0X1"));
        assertNotNull(AcerRules.validatePartNo("LU.AWWA0.0X1s"));
        assertNotNull(AcerRules.validatePartNo(" LU.AWWA0.0X"));
        assertNotNull(AcerRules.validatePartNo(" L.AWWA0.0X2"));
        assertNotNull(AcerRules.validatePartNo(""));
        assertNotNull(AcerRules.validatePartNo(null));
    }

    @Test
    public void testValidateSerial() {
        serialErrorAndWarn(null);
        serialErrorAndWarn("AAB");
        serialErrorAndWarn("ABBABABaASDF AFDAS");
        serialErrorAndWarn("AABASDFDSAFAÖOFDSAFDSa");
        serialWarn("LXN610Y1239342BF0616");
        serialWarn("LXN610Y1239342BF0616000001");
        serialWarn("LXN610Y123A342BF061601");
        serialWarn("LXN610Y1239F42BF061601");
        serialOk("ABADADFA12345OOOFDSAFD");
        serialOk("LXN610Y1239342BF061601");
        serialOk("EVM2507001709007E84652");
        serialOk("EVM2707001636001EB4610");
        serialOk("LXPBB0X240928009102000");
        serialOk("LXPBB0X24092805B742000");
        serialOk("LXPBB0X240928021D32000");
        serialOk("LXPBB0X24092800A7F2000");
        serialOk("LXPBB0X2409280EFAE2000");
        serialOk("LXN610Y1239291FA351601");
        serialOk("LXN610Y1239342AF591601");
        serialOk("LXN610Y123929201391601");
    }

    private void serialErrorAndWarn(String s) {
        assertNotNull("Serial:" + s + " should validate to Error", AcerRules.validateSerialError(s));
        assertNotNull("Serial:" + s + " should validate to Warning", AcerRules.validateSerialWarning(s));
    }

    private void serialWarn(String s) {
        assertNull("Serial:" + s + " should not have " + AcerRules.validateSerialError(s), AcerRules.validateSerialError(s));
        assertNotNull("Serial:" + s + " should validate to Warning", AcerRules.validateSerialWarning(s));
    }

    private void serialOk(String s) {
        assertNull("Serial:" + s + " should not have " + AcerRules.validateSerialError(s), AcerRules.validateSerialError(s));
        assertNull("Serial:" + s + " should not have " + AcerRules.validateSerialWarning(s), AcerRules.validateSerialWarning(s));
    }

    @Test
    @Ignore
    public void testMfgDateFromSerial() {
        System.out.println("Year=0 - " + AcerRules.mfgDateFromSerial("LXPBB0X240028021D32000")); // Y = 0
        System.out.println("Year=1 - " + AcerRules.mfgDateFromSerial("LXPBB0X24012800A7F2000")); // Y = 1
        System.out.println("Year=2 - " + AcerRules.mfgDateFromSerial("LXPBB0X2402280EFAE2000")); // Y = 2
        System.out.println("Year=3 - " + AcerRules.mfgDateFromSerial("LXN610Y1233291FA351601")); // Y = 3
        System.out.println("Year=4 - " + AcerRules.mfgDateFromSerial("LXN610Y1234342AF591601")); // Y = 4
        System.out.println("Year=5 - " + AcerRules.mfgDateFromSerial("LXN610Y1235342AF591601")); // Y = 5
        System.out.println("Year=6 - " + AcerRules.mfgDateFromSerial("LXN610Y1236342AF591601")); // Y = 6
        System.out.println("Year=7 - " + AcerRules.mfgDateFromSerial("LXN610Y1237342AF591601")); // Y = 7
        System.out.println("Year=8 - " + AcerRules.mfgDateFromSerial("LXN610Y1238342AF591601")); // Y = 8
        System.out.println("Year=9 - " + AcerRules.mfgDateFromSerial("LXN610Y123243201391601")); // Y = 9
        System.out.println(AcerRules.mfgDateFromSerial("LXN610Y123911201391601"));
        System.out.println(AcerRules.mfgDateFromSerial("LXN610Y123902201391601"));
        System.out.println(AcerRules.mfgDateFromSerial("LXN610Y123942201391601"));
    }
}
