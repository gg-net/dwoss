/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ee.listings;

import java.io.File;
import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.FileSystemFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.progress.IMonitor;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.mandator.api.service.UploadConfiguration.ConnectionConfig;
import eu.ggnet.dwoss.mandator.api.service.UploadConfiguration.UploadCommand;

/**
 *
 * @author oliver.guenther
 */
public class ScpUpload {

    private final static Logger L = LoggerFactory.getLogger(ScpUpload.class);

    public void upload(ConnectionConfig config, IMonitor monitor, UploadCommand... uploads) throws IOException, ClassNotFoundException {
        if ( uploads == null || uploads.length == 0 ) return;
        SubMonitor m = SubMonitor.convert(monitor, "SCP Transfer", toWorkSize(uploads) + 4);
        m.message("verbinde");
        m.start();

        SSHClient ssh = new SSHClient();
        if ( config.hostKey != null ) ssh.addHostKeyVerifier(config.hostKey);
        ssh.connect(config.host, config.port);

        try {
            ssh.authPassword(config.user, config.pass);

            for (UploadCommand upload : uploads) {
                m.worked(1, "deleting old lists in " + upload.getPath());
                Session sshSession = ssh.startSession();
                for (String deleteFileType : upload.getDeleteFileTypes()) {
                    String command = "/usr/bin/rm -f *." + deleteFileType;
                    L.info("upload() excuting remote command:{}", command);
                    sshSession.exec(command);
                }

                for (File file : upload.getFiles()) {
                    m.worked(1, "uploading to " + upload.getPath() + " file " + file.getName());
                    L.info("upload() uploading file:{}", file.getName());
                    ssh.newSCPFileTransfer().upload(new FileSystemFile(file), ".");
                }

            }

        } finally {
            m.finish();
            ssh.disconnect();
        }
    }

    private int toWorkSize(UploadCommand[] uploads) {
        int size = uploads.length;
        for (UploadCommand upload : uploads) {
            size += upload.getFiles().size();
        }
        return size;
    }

}
