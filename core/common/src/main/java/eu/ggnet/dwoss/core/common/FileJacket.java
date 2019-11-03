/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.core.common;

import java.io.*;
import java.nio.file.*;

/**
 * A Container to transfer File contents
 * <p>
 * @author oliver.guenther
 */
public class FileJacket implements Serializable {

    private byte[] content;

    private String head;

    private String suffix;

    /**
     * Constructor to set the content directly.
     *
     * @param head    the head of the filename
     * @param suffix  the suffix of the file
     * @param content the content of the file
     */
    public FileJacket(String head, String suffix, byte[] content) {
        this.content = content;
        this.head = head;
        this.suffix = suffix;
    }

    /**
     * Constructur with read in logic.
     * Creates a new DataFile with the content of the supplied file
     * <p>
     * @param head   the head of the filename
     * @param suffix the suffix of the file
     * @param file   the file to read
     * @throws RuntimeException if something while reading the file fails.
     */
    public FileJacket(String head, String suffix, File file) throws RuntimeException {
        this.head = head;
        this.suffix = suffix;
        content = new byte[0];
        if ( file == null ) throw new NullPointerException("File is null");
        if ( !file.exists() || !file.canRead() ) throw new IllegalStateException(file + " dosn't exist or cannot be read");
        try {
            content = Files.readAllBytes(Paths.get(file.toURI()));
        } catch (IOException ex) {
            throw new RuntimeException("Exception during Read File", ex);
        }
    }

    /**
     * Creates a temporary File with the content of this DataFile, deletes on exit.
     * <p>
     * @return the file pointing to the temporary one.
     */
    public File toTemporaryFile() {
        try {
            Path p = Files.createTempFile(head + "_", suffix);
            Files.write(p, content);
            File f = p.toFile();
            f.deleteOnExit();
            return f;
        } catch (IOException ex) {
            throw new RuntimeException("Temporary File creation not done.", ex);
        }
    }

    /**
     * Creates a File from the FileJacket and stores it.
     * The File name will be FileJacket.head + Filejacket.suffix
     * <p>
     * @param url the location the File is stored to
     * @return the File
     */
    public File toFile(String url) {
        try {
            File f = new File(url + "/" + this.head + this.suffix);
            Path p = f.toPath();
            Files.write(p, content);
            return p.toFile();
        } catch (IOException e) {
            throw new RuntimeException("File Creation Unseccessful", e);
        }
    }

    public byte[] getContent() {
        return content;
    }

    public String getHead() {
        return head;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return "FileJacket{" + ", head=" + head + ", suffix=" + suffix + "content.length=" + content.length + '}';
    }

}
