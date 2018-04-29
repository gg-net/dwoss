package tryout.support;


import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.progress.HiddenMonitor;
import eu.ggnet.dwoss.progress.ProgressObserver;

/*
 * Copyright (C) 2014 GG-Net GmbH
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
/**
 *
 * @author oliver.guenther
 */
public class ProgressObserverStub implements ProgressObserver{

    private final Map<Integer,HiddenMonitor> monitors = new HashMap<>();
    
    @Override
    public SortedSet<Integer> getActiveProgressKeys() {
        return monitors.entrySet()
                .stream()
                .filter(e -> !e.getValue().isFinished())
                .map(e -> e.getKey())
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    public void add(HiddenMonitor m) {
        monitors.put(m.hashCode(), m);
    }
    
    @Override
    public HiddenMonitor getMonitor(int key) {
        return monitors.get(key);
    }

    @Override
    public boolean hasProgress() {
        return monitors.entrySet()
                .stream()
                .anyMatch(e -> !e.getValue().isFinished());                
    }
    
}
