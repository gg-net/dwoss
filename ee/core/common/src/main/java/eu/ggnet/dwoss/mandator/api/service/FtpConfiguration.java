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
