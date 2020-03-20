
/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support.login;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * Class to store and load the Timeout and if it's active.
 * Hint: In the JPro version, this can be specalized, thats why CDI is used.
 *
 * @author oliver.guenther
 */
public class LoggedInTimeoutStorage {

    @Inject
    private Logger log;

    private final Preferences P = Preferences.userNodeForPackage(LoggedInTimeoutStorage.class);

    private final DateTimeFormatter TF = DateTimeFormatter.ofPattern("mm:ss");

    private final String TIME_OUT = "timeout";

    private final String ACTIVE = "active";

    /**
     * Loads the timeout from storage or the default of 3 minutes.
     *
     * @return the timeout from storage or the default of 3 minutes.
     */
    public LocalTime loadTimeout() {
        TemporalAccessor result = TF.parse(P.get(TIME_OUT, "03:00"));
        return LocalTime.of(0, result.get(ChronoField.MINUTE_OF_HOUR), result.get(ChronoField.SECOND_OF_MINUTE));
    }

    /**
     * Tries to store the timeout, if it fails only a log is written.
     *
     * @param localTime the time to store.
     */
    public void storeTimeOut(LocalTime localTime) {
        log.debug("storeTimeOut({})", localTime);
        P.put(TIME_OUT, TF.format(localTime));
        flush();
    }

    /**
     * Loads the status form the storage or returns the default of true.
     *
     * @return the status
     */
    public boolean loadActive() {
        return P.getBoolean(ACTIVE, true);
    }

    /**
     * Tries to store the active setting, if it fails only a log is written.
     *
     * @param active
     */
    public void storeActive(boolean active) {
        log.debug("storeActive({})", active);
        P.putBoolean(ACTIVE, active);
        flush();
    }

    private void flush() {
        try {
            P.flush();
        } catch (BackingStoreException ex) {
            log.error("Cound not store Preferences", ex);
        }
    }

}
