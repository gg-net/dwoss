/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.web.stub;

import eu.ggnet.dwoss.core.system.progress.ProgressObserver;
import eu.ggnet.dwoss.core.system.progress.HiddenMonitor;

import java.util.*;


import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * build a Stub with random scheduled progress
 *
 * @author jens.papenhagen
 */
@Named
@Stateless
public class MonitorStub implements ProgressObserver {

    private final static Logger LOG = LoggerFactory.getLogger(MonitorStub.class);
    
    private final Map<Integer, HiddenMonitor> monitors = new HashMap<>();

    @Override
    public SortedSet<Integer> getActiveProgressKeys() {
        return monitors.entrySet().stream()
                .filter(e -> !e.getValue().isFinished())
                .map(e -> e.getKey())
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    @Override
    public HiddenMonitor getMonitor(int key) {
        return monitors.get(key);
    }

    public void setMonitor(HiddenMonitor m) {
        monitors.put(m.hashCode(), m);
    }

    @Override
    public boolean hasProgress() {
        return monitors.entrySet().stream()
                .anyMatch(e -> !e.getValue().isFinished());
    }

    public void buttonStartSampleAction(ActionEvent actionEvent) {
        //       Runnable task = () -> {
        
        HiddenMonitor hm = new HiddenMonitor();

        hm.title("TestMonitor");
        hm.setMessage("Progress 000");

        setMonitor(hm);
        hm.start();
        
        for (int i = 0; i < 100; i++) {
            hm.worked(1, "Done: " + i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                LOG.debug("Problems with the sleep Thread");
            }
        }
        hm.finish();

//        };
                //        ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
                //        ScheduledFuture<?> future = executor.schedule(task, new Random().nextLong(), TimeUnit.SECONDS);
                //
                //        try {
                //            future.get();
                //        } catch (InterruptedException | ExecutionException ex) {
                //            System.out.println("InterruptedException or ExecutionException form Future");
                //        }
    }

}
