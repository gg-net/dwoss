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
package eu.ggnet.dwoss.assembly.client;

/**
 *
 * @author oliver.guenther
 */
public class DwOssMainWithSampleConfig {

    public static void main(String[] args) {
        DwOssMain.main(new String[]{"--protocol=remote+http", "--host=192.168.2.32", "--port=8080", "--app=dwoss-server-sample", "--user=admin", "--pass=admin"});
    }

}
