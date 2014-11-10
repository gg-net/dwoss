package eu.ggnet.dwoss.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Warranty.
 * <p/>
 * @author oliver.guenther
 */
@RequiredArgsConstructor
public enum Warranty {

    ONE_YEAR_CARRY_IN("1 Jahr Bring-In Garantie"), ONE_YEAR_CARRY_IN_ADVANCED("1 Jahr Bring-In, Verlängerung mögl."),
    FOURTEEN_DAYS_FUNTION_WARRANTY("14 Tage Funktionsgarantie"), TWO_YEARS_CARRY_IN("2 Jahre Bring-In Garantie"), NO_WARRANTY("Keine Garantie"),
    WARRANTY_TILL_DATE("Garantie bis Datum");

    @Getter
    private final String name;

    public static Warranty getWarrantyById(int id) {
        if (id < 0 || id >= Warranty.values().length) return null;
        return Warranty.values()[id];
    }
}
