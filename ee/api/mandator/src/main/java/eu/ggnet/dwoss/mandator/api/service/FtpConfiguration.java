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
package eu.ggnet.dwoss.mandator.api.service;

import java.io.File;
import java.util.*;

/**
 *
 * @author pascal.perau
 */
public class FtpConfiguration {

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
        private final String path;

        /**
         * An optional list of file extensions, which should be removed in these folders.
         */
        private final Set<String> deleteFileTypes = new HashSet<>();

        public Set<File> getFiles() {
            return files;
        }

        public String getPath() {
            return path;
        }

        public Set<String> getDeleteFileTypes() {
            return deleteFileTypes;
        }

        public void add(File file) {
            files.add(file);
        }

        public void deleteType(String type) {
            deleteFileTypes.add(type);
        }

        @Override
        public String toString() {
            return "UploadCommand{" + "files=" + files + ", path=" + path + ", deleteFileTypes=" + deleteFileTypes + '}';
        }

    }

    public static class ConnectionConfig {

        public final String host;

        public final int port;

        public final String user;

        public final String pass;

        public ConnectionConfig(String host, int port, String user, String pass) {
            this.host = Objects.requireNonNull(host, "new ConnectionConfig with host=null called, not allowed");
            this.port = port;
            this.user = Objects.requireNonNull(user, "new ConnectionConfig with user=null called, not allowed");;
            this.pass = Objects.requireNonNull(pass, "new ConnectionConfig with pass=null called, not allowed");;
        }

        @Override
        public String toString() {
            return "ConnectionConfig{" + "host=" + host + ", port=" + port + ", user=" + user + ", pass=" + pass + '}';
        }

    }

    private final ConnectionConfig config;

    private final Set<UploadCommand> updloadCommands;

    public FtpConfiguration(ConnectionConfig config, Set<UploadCommand> updloadCommands) {
        this.config = Objects.requireNonNull(config, "new FtpConfiguration with config=null called, not allowed");;
        this.updloadCommands = Objects.requireNonNull(updloadCommands, "new FtpConfiguration with updloadCommands=null called, not allowed");;
    }

    public ConnectionConfig getConfig() {
        return config;
    }

    public Set<UploadCommand> getUpdloadCommands() {
        return updloadCommands;
    }

    @Override
    public String toString() {
        return "FtpConfiguration{" + "config=" + config + ", updloadCommands=" + updloadCommands + '}';
    }

}
