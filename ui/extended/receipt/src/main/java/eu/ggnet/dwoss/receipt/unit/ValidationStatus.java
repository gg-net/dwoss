package eu.ggnet.dwoss.receipt.unit;

import java.awt.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Status of a Validation, contains a color to be used in the ui.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public enum ValidationStatus {

    /**
     * OK(Color.BLACK).
     */
    OK(Color.BLACK),
    /**
     * VALIDATING(Color.CYAN).
     */
    VALIDATING(Color.CYAN),
    /**
     * WARNING(Color.ORANGE).
     */
    WARNING(Color.ORANGE),
    /**
     * ERROR(Color.RED).
     */
    ERROR(Color.RED);

    /**
     * A color representing the status.
     */
    @Getter
    private final Color color;

}
