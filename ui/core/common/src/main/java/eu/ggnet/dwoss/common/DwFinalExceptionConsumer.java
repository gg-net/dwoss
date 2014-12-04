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
package eu.ggnet.dwoss.common;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import eu.ggnet.saft.core.SwingCore;

import static eu.ggnet.dwoss.common.DwOssCore.getUserInfo;
import static eu.ggnet.saft.core.exception.ExceptionUtil.*;

/**
 *
 * @author oliver.guenther
 */
public class DwFinalExceptionConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable b) {
        Runnable r = () -> {
            DetailDialog.show(SwingCore.mainFrame(), "Systemfehler", extractDeepestMessage(b),
                    getUserInfo() + '\n' + toMultilineStacktraceMessages(b), getUserInfo() + '\n' + toStackStrace(b));
        };

        if ( EventQueue.isDispatchThread() ) r.run();
        else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (InterruptedException | InvocationTargetException e) {
                // This will never happen.
            }
        }
    }

}
