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
import java.util.Deque;

/**
 *
 * @author bastian.venz
 */
public class QueueStore {

    public static void store(Deque<QueueElement> queue, FileOutputStream fileOutputStream) throws IOException {
        try (ObjectOutputStream os = new ObjectOutputStream(fileOutputStream);) {
            os.writeObject(queue);
        } catch (IOException exception) {
            throw exception;
        }
    }

    public static Deque<QueueElement> load(FileInputStream fileInputStream) throws IOException {
        try (ObjectInputStream is = new ObjectInputStream(fileInputStream);) {
            Deque<QueueElement> queue = (Deque<QueueElement>)is.readObject();
            return queue;
        } catch (IOException exception) {
            throw exception;

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Class was not found.", ex);
        }
    }

}
