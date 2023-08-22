package test;

import org.junit.Test;

import eu.ggnet.dwoss.core.common.values.AppleRules;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AppleRulesTest {

    @Test
    public void testValidatePartNo() {
        String[] validPartNos = {"MC769FD/A", "MC979FD/A", "MC773FD/A", "MC770FD/A", "MC980FD/A", "MC774FD/A", "MC983FD/A", "MC916FD/A", "MC981FD/A",
            "MC775FD/A", "MC984FD/A", "MD510FD/A", "MD513FD/A", "MD522FD/A", "MD525FD/A", "MD511FD/A", "MD514FD/A", "MD523FD/A", "MD526FD/A",
            "MD512FD/A", "MD515FD/A", "MD524FD/A", "MD527FD/A", "MD531FD/A", "MD541FD/A", "MD530FD/A", "MD542FD/A", "MD545FD/A", "MD198DN/A",
            "MD235D/A", "MD239D/A", "MD297DN/A", "MD102D/A", "MD101D/A", "MD103D/A", "MD387D/A", "MC975D/A", "Z0RS00054", "Z0RT000BN", "M00H3FD/A",
            "M00J3FD/A", "M00M3FD/A", "M06Q3FD/A", "M00A3FD/A", "M09J3FD/A",
            "Z12S0008J",
            "Z1300008J",
            "Z14800043",
            "Z148001AX",
            "Z1450002E"
        };
        String[] invalidPartNos = {null, "", "1C769FD/A", "MC979FD", "1P773fd/A", "_MC770FD/A", "MC98  0FD/A", "  MC774FD/A",
            "MC983FD/A  ", "()MC916FD/A", "=MC981FD/A", "Z0RT00EBNN", "Z0R000BN", "Z0RT000N", "Z0RT0000BN"};

        for (String validPartNo : validPartNos) {
            assertValidPartNo(validPartNo);
        }

        for (String invalidPartNo : invalidPartNos) {
            assertInvalidPartNo(invalidPartNo);
        }
    }

    private void assertValidPartNo(String partNo) {
        assertNull("Valid PartNo " + partNo + " is invalid:" + AppleRules.validatePartNo(partNo),
                AppleRules.validatePartNo(partNo));
    }

    private void assertInvalidPartNo(String partNo) {
        assertNotNull("Invalid PartNo " + partNo + " is valid", AppleRules.validatePartNo(partNo));
    }

    @Test
    public void testValidateSerial() {
        String[] validSerials = {"DMVJR9XHF185", "DMPJNTSLDFHW", "DMPJMX2EDKPH", "DMPJMWA0DKPH", "DMPJMDXCF191", "DN6GWB6MDKPK", "DLXJTFDAF19L", "DLXJM1PSF195",
            "F4KJP0ULF19P", "DLXJQ3D9F182", "DMQJJK00DKPH", "DMPJRDDHDKPH", "DMQJJZTZDKPH", "DLXJP0MXF185", "DMPJNWPTDFHW", "DMRJNH49DFHW", "DLXJP5ZQF185",
            "DMQJD33HDFHW", "DMQJDWQ2DFHW", "DMPJNXV8DFHW", "DMRJN9GQDFHW", "DMRJN7JSDFHW", "DMRJN9N9DFHW", "DMQJN8PCDFHW", "DMRJJ4AGDKPH", "DMPJNTRPDFHW",
            "DMPJPNVCDKPH", "DMPJPPEUDKPH", "DLXJK55CF183", "DMQJPH9GF186", "DQTJLSJCF185", "DQTJPFCXF185", "DLXJP5HFF186", "DMPJJC4RF183", "DQTJPJ0QF185",
            "DMQJPHMGF186", "DLXJM2WWF185", "8K348DYG9ZU", "8K348DYG9Z", "XPVW29FV34", "Z12N00006", "Z12P0007P", "Z12N00003", "Z12N0006D"};

        String[] invalidSerials = {null, "", "  DMPJMDXCF191", "DN6GWB6MDKPK  "};

        for (String validSerial : validSerials) {
            assertValidSerial(validSerial);
        }

        for (String invalidSerial : invalidSerials) {
            assertInvalidSerial(invalidSerial);
        }
    }

    private void assertValidSerial(String serial) {
        assertNull("Valid Serial " + serial + " is invalid:" + AppleRules.validateSerial(serial),
                AppleRules.validateSerial(serial));
    }

    private void assertInvalidSerial(String serial) {
        assertNotNull("Invalid Serial " + serial + " is valid", AppleRules.validateSerial(serial));
    }
}
