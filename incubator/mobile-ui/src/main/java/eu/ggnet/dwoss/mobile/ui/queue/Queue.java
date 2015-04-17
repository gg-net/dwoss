/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.mobile.ui.queue;

import java.io.*;
import java.util.ArrayDeque;

import android.content.Context;

/**
 *
 * @author bastian.venz
 */
public class Queue {

    private ArrayDeque<QueueElement> queue = new ArrayDeque<>();

    private final Context context;

    private static final String FILENAME = "queue.save";

    public Queue(Context context) {
        this.context = context;
        if ( context != null ) load();
    }

    public QueueElement poll() {
        QueueElement poll = queue.poll();
        save();
        return poll;
    }

    public void push(QueueElement element) {
        queue.push(element);
        save();
    }

    private void save() {
        if ( context != null ) {
            try (FileOutputStream openFileOutput = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);) {
                QueueStore.store(queue, openFileOutput);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void load() {
        if ( context != null ) {
            try (FileInputStream fis = context.openFileInput(FILENAME);) {
                queue = new ArrayDeque<>(QueueStore.load(fis));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
