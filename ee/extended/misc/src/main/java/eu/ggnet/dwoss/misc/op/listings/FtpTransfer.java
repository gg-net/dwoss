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
package eu.ggnet.dwoss.misc.op.listings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Collection;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.api.service.FtpConfiguration.ConnectionConfig;
import eu.ggnet.dwoss.mandator.api.service.FtpConfiguration.UploadCommand;

import eu.ggnet.saft.api.progress.IMonitor;

import eu.ggnet.dwoss.progress.SubMonitor;

/**
 * Plain Logic for file transfer operations.
 *
 * @author oliver.guenther
 */
public class FtpTransfer {

    private final static ProtocolCommandListener PROTOCOL_TO_LOGGER = new ProtocolCommandListener() {
        @Override
        public void protocolCommandSent(ProtocolCommandEvent event) {
            L.info(event.getMessage());
        }

        @Override
        public void protocolReplyReceived(ProtocolCommandEvent event) {
            L.info(event.getMessage());
        }
    };

    private final static Logger L = LoggerFactory.getLogger(FtpTransfer.class);

    /**
     * Uploads a some files to a remove ftp host.
     * <p/>
     * @param config  the config for he connection.
     * @param uploads the upload commands
     * @param monitor an optional monitor.
     * @throws java.net.SocketException
     * @throws java.io.IOException
     */
    public static void upload(ConnectionConfig config, IMonitor monitor, UploadCommand... uploads) throws SocketException, IOException {
        if ( uploads == null || uploads.length == 0 ) return;
        SubMonitor m = SubMonitor.convert(monitor, "FTP Transfer", toWorkSize(uploads) + 4);
        m.message("verbinde");
        m.start();
        FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(PROTOCOL_TO_LOGGER);

        try {
            ftp.connect(config.getHost(), config.getPort());
            if ( !FTPReply.isPositiveCompletion(ftp.getReplyCode()) ) throw new IOException("FTPReply.isPositiveCompletion(ftp.getReplyCode()) = false");
            if ( !ftp.login(config.getUser(), config.getPass()) ) throw new IOException("Login with " + config.getUser() + " not successful");
            L.info("Connected to {} idenfied by {}", config.getHost(), ftp.getSystemType());

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            for (UploadCommand upload : uploads) {
                m.worked(1, "uploading to " + upload.getPath());
                ftp.changeWorkingDirectory(upload.getPath());
                deleteFilesByType(ftp, upload.getDeleteFileTypes());
                for (File file : upload.getFiles()) {
                    m.worked(1, "uploading to " + upload.getPath() + " file " + file.getName());
                    try (InputStream input = new FileInputStream(file)) {
                        if ( !ftp.storeFile(file.getName(), input) ) throw new IOException("Cannot store file " + file + " on server!");
                    }
                }
            }
            m.finish();
        } finally {
            // just cleaning up
            try {
                ftp.logout();
            } catch (IOException e) {
            }
            if ( ftp.isConnected() ) {
                try {
                    ftp.disconnect();
                } catch (IOException f) {
                }
            }
        }
    }

    private static void deleteFilesByType(FTPClient ftp, Collection<String> fileTypesToDelete) throws IOException {
        if ( fileTypesToDelete == null || fileTypesToDelete.isEmpty() ) return;
        FTPFile[] existingFiles = ftp.listFiles();
        if ( existingFiles == null || existingFiles.length == 0 ) return;
        for (String type : fileTypesToDelete) {
            for (FTPFile ftpFile : existingFiles) {
                if ( ftpFile == null ) continue;
                if ( ftpFile.getName().toLowerCase().endsWith(type) ) {
                    if ( !ftp.deleteFile(ftpFile.getName()) ) {
                        throw new IOException("Could not delete " + ftpFile.getName());
                    }
                }
            }
        }
    }

    private static int toWorkSize(UploadCommand[] uploads) {
        int size = uploads.length;
        for (UploadCommand upload : uploads) {
            size += upload.getFiles().size();
        }
        return size;
    }
}
