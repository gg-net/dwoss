/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.progress.support;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.LoggerFactory;

/**
 *
 * @author olive
 */
@Stateless
public class MonitorFactorySupportBean {

    @Inject
    private MonitorFactory monitorFactory;

    public void doSomething() {
        LoggerFactory.getLogger(MonitorFactorySupportBean.class).info("doSomething called");
        SubMonitor m = monitorFactory.newSubMonitor("The Test Progress", 100);
        for (int i = 0; i < 100;
                i++) {
            m.worked(1, "Done: " + i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        m.finish();
    }

}
