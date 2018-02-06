/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.core.ui;

import javax.swing.JOptionPane;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Type of an Alert.
 *
 * @author oliver.guenther
 */
@AllArgsConstructor
public enum AlertType {
    INFO(JOptionPane.INFORMATION_MESSAGE), WARNING(JOptionPane.WARNING_MESSAGE), ERROR(JOptionPane.ERROR_MESSAGE);

    @Getter
    private final int optionPaneType;

}
