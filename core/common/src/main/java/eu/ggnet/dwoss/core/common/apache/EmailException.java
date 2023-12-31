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

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Exception thrown when a checked error occurs in commons-email.
 * <p>
 * Adapted from FunctorException in Commons Collections.
 * <p>
 * Emulation support for nested exceptions has been removed in {@code Email 1.3},
 * supported by JDK &ge; 1.4.
 *
 * @since 1.0
 */
public class EmailException
        extends Exception
{
    /** Serializable version identifier. */
    private static final long serialVersionUID = 5550674499282474616L;

    /**
     * Constructs a new {@code EmailException} with no
     * detail message.
     */
    public EmailException()
    {
    }

    /**
     * Constructs a new {@code EmailException} with specified
     * detail message.
     *
     * @param msg  the error message.
     */
    public EmailException(final String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new {@code EmailException} with specified
     * nested {@code Throwable} root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public EmailException(final Throwable rootCause)
    {
        super(rootCause);
    }

    /**
     * Constructs a new {@code EmailException} with specified
     * detail message and nested {@code Throwable} root cause.
     *
     * @param msg  the error message.
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public EmailException(final String msg, final Throwable rootCause)
    {
        super(msg, rootCause);
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    @Override
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace of this exception to the specified stream.
     *
     * @param out  the {@code PrintStream} to use for output
     */
    @Override
    public void printStackTrace(final PrintStream out)
    {
        synchronized (out)
        {
            final PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, Charset.defaultCharset()), false);
            printStackTrace(pw);

            // Flush the PrintWriter before it's GC'ed.
            pw.flush();
        }
    }

    /**
     * Prints the stack trace of this exception to the specified writer.
     *
     * @param out  the {@code PrintWriter} to use for output
     */
    @Override
    public void printStackTrace(final PrintWriter out)
    {
        synchronized (out)
        {
            super.printStackTrace(out);
        }
    }
}