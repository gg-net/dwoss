/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.rights.ee.op;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for passwords.
 *
 * @author oliver.guenther
 */
public final class PasswordUtil {

    /**
     * This hash the given password with the given salt.
     *
     * @param password is the readable Password.
     * @param salt     is the Salt that will used to salt the password.
     * @return return the hashed and salted password.
     */
    public static byte[] hashPassword(char[] password, byte[] salt) {
        if ( password == null || password.length == 0 ) throw new NullPointerException("password is null or empty");
        if ( salt == null || salt.length == 0 ) throw new NullPointerException("salt is null or empty");

        // TODO: If we want to be real secure, never create a String
        String pw = String.valueOf(password);
        if ( pw.isBlank() ) throw new IllegalArgumentException("password is blank");

        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(pw.getBytes("UTF-8"));
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 255) + 256, 16).substring(1));
            }
            return sb.toString().getBytes("UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
