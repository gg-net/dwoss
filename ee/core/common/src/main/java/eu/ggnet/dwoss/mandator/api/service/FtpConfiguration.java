/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.mandator.api.service;

import java.io.File;
import java.util.*;

import lombok.Data;

/**
 *
 * @author pascal.perau
 */
@Data
public class FtpConfiguration {

    @Data
    public static class UploadCommand {

        /**
         * Constructor.
         *
         * @param path        the path on the host
         * @param deleteTypes the types to delete before.
         * @param files
         */
        public UploadCommand(String path, Collection<String> deleteTypes, Collection<File> files) {
            this.path = path;
            if ( deleteTypes != null && !deleteTypes.isEmpty() ) this.deleteFileTypes.addAll(deleteTypes);
            if ( files != null && !files.isEmpty() ) this.files.addAll(files);
        }

        /**
         * Local files to be uploaded.
         */
        private final Set<File> files = new HashSet<>();

        /**
         * The path on the ftp host to use.
         */
        private String path;

        /**
         * An optional list of file extensions, which should be removed in these folders.
         */
        private final Set<String> deleteFileTypes = new HashSet<>();

        public void add(File file) {
            files.add(file);
        }

        public void deleteType(String type) {
            deleteFileTypes.add(type);
        }
    }

    @Data
    public static class ConnectionConfig {

        private final String host;

        private final int port;

        private final String user;

        private final String pass;
    }

    private final ConnectionConfig config;

    private final Set<UploadCommand> updloadCommands;

}
