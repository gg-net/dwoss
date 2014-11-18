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
package eu.ggnet.dwoss.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class ImageFinder {

    private final File path;

    private final URL noimageUrl;

    private URL errorUrl;

    public ImageFinder(String filePath) {
        this.path = filePath == null ? null : new File(filePath);

        //Setup the error URL
        try {
            File errorFile = File.createTempFile("error", ".png");
            errorFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("error.png"), errorFile);
            errorUrl = errorFile.toURI().toURL();
        } catch (IOException ex) {
            throw new RuntimeException("Error in activating Fallback mode.", ex);
        }

        // The No Image.
        try {
            File noimageFile = File.createTempFile("noimage", ".png");
            noimageFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("noimage.png"), noimageFile);
            noimageUrl = noimageFile.toURI().toURL();
        } catch (IOException ex) {
            throw new RuntimeException("Error in creating the noimgae", ex);
        }

    }

    public URL findImageUrl(final int id) {
        if ( path == null || (!path.exists() && !path.isDirectory()) ) return errorUrl;
        String images[] = path.list((dir, name) -> {
            if ( name.toLowerCase().endsWith("_" + id + ".jpg") ) return true;
            if ( name.toLowerCase().endsWith("_" + id + ".gif") ) return true;
            if ( name.toLowerCase().endsWith("_" + id + ".png") ) return true;
            return false;
        });
        if ( images == null || images.length == 0 ) return noimageUrl;
        try {
            return new File(path, images[0]).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error in URL creation", e);
        }
    }

    public URL findImageUrlByName(final String head) {
        if ( path == null || (!path.exists() && !path.isDirectory()) ) return errorUrl;
        String images[] = path.list((dir, name) -> {
            if ( name.equalsIgnoreCase(head + ".jpg") ) return true;
            if ( name.equalsIgnoreCase(head + ".gif") ) return true;
            if ( name.equalsIgnoreCase(head + ".png") ) return true;
            return false;
        });
        if ( images == null || images.length == 0 ) return noimageUrl;
        try {
            return new File(path, images[0]).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error in URL creation", e);
        }
    }

    public int nextImageId() {
        if ( path == null || (!path.exists() && !path.isDirectory()) ) return -1;
        int max = 0;
        for (String name : path.list()) {
            StringBuilder rev = new StringBuilder(name);
            rev.reverse();
            int p = rev.indexOf(".");
            if ( p < 0 ) continue;
            rev.delete(0, p + 1);
            p = rev.indexOf("_");
            if ( p < 0 ) continue;
            rev.delete(p, rev.length());
            rev.reverse();
            int val = 0;
            try {
                val = Integer.parseInt(rev.toString());
            } catch (NumberFormatException e) {
                continue;
            }
            if ( max < val ) max = val;
        }
        return max + 1;
    }
}
