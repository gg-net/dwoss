/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.core.common.apache;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/**
 * Static helper methods.
 *
 * @since 1.3
 */
public final class MimeMessageUtils
{
    /**
     * Instances should NOT be constructed in standard programming.
     */
    private MimeMessageUtils()
    {
    }

    /**
     * Creates a MimeMessage.
     *
     * @param session the mail session
     * @param source the input data
     * @return the MimeMessage
     * @throws MessagingException creating the MimeMessage failed
     * @throws IOException creating the MimeMessage failed
     */
    public static MimeMessage createMimeMessage(final Session session, final byte[] source)
        throws MessagingException, IOException
    {
        try (ByteArrayInputStream is = new ByteArrayInputStream(source))
        {
            return new MimeMessage(session, is);
        }
    }

    /**
     * Creates a MimeMessage.
     *
     * @param session the mail session
     * @param source the input data
     * @return the MimeMessage
     * @throws MessagingException creating the MimeMessage failed
     * @throws IOException creating the MimeMessage failed
     */
    public static MimeMessage createMimeMessage(final Session session, final File source)
        throws MessagingException, IOException
    {
        try (FileInputStream is = new FileInputStream(source))
        {
            return createMimeMessage(session, is);
        }
    }

    /**
     * Creates a MimeMessage.
     *
     * @param session the mail session
     * @param source the input data
     * @return the MimeMessage
     * @throws MessagingException creating the MimeMessage failed
     */
    public static MimeMessage createMimeMessage(final Session session, final InputStream source)
        throws MessagingException
    {
        return new MimeMessage(session, source);
    }

    /**
     * Creates a MimeMessage using the platform's default character encoding.
     *
     * @param session the mail session
     * @param source the input data
     * @return the MimeMessage
     * @throws MessagingException creating the MimeMessage failed
     * @throws IOException creating the MimeMessage failed
     */
    public static MimeMessage createMimeMessage(final Session session, final String source)
        throws MessagingException, IOException
    {
        ByteArrayInputStream is = null;

        try
        {
            final byte[] byteSource = source.getBytes(Charset.defaultCharset());
            is = new ByteArrayInputStream(byteSource);
            return createMimeMessage(session, is);
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    /**
     * Writes a MimeMessage into a file.
     *
     * @param mimeMessage the MimeMessage to write
     * @param resultFile  the file containing the MimeMessgae
     * @throws MessagingException accessing MimeMessage failed
     * @throws IOException        writing the MimeMessage failed
     */
    public static void writeMimeMessage(final MimeMessage mimeMessage, final File resultFile)
        throws MessagingException, IOException
    {
        if (!resultFile.getParentFile().exists() && !resultFile.getParentFile().mkdirs())
        {
            throw new IOException(
                    "Failed to create the following parent directories: "
                            + resultFile.getParentFile());
        }
        try (FileOutputStream fos = new FileOutputStream(resultFile)) {
            mimeMessage.writeTo(fos);
            fos.flush();
        }
    }
}