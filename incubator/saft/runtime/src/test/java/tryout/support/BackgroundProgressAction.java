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
package tryout.support;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import eu.ggnet.saft.api.progress.HiddenMonitor;
import eu.ggnet.saft.core.Ui;

/**
 *
 * @author oliver.guenther
 */
public class BackgroundProgressAction extends AbstractAction {

    private final ProgressObserverStub progressObserver;
    
    public BackgroundProgressAction(ProgressObserverStub progressObserver) {
        super("Saft and Background Progress 10s");
        this.progressObserver = progressObserver;
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        Ui.call(() -> {
            HiddenMonitor m = new HiddenMonitor();
            m.title("TestMonitor");
            progressObserver.add(m);
            m.start();
            for (int i = 0; i < 20; i++) {
                m.worked(5, "Working on " + i);
                Thread.sleep(500);
            }
            m.finish();            
            return null;
        }).exec();
    }
    
}
