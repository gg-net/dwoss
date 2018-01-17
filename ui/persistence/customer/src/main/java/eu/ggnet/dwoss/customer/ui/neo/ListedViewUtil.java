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
package eu.ggnet.dwoss.customer.ui.neo;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Have the Buttons Image Properties only on one piont.
 *
 * @author jens.papenhagen
 */
public class ListedViewUtil {

    public ImageView addButton() {
        ImageView addImg = new ImageView();
        addImg.setFitHeight(24.0);
        addImg.setFitWidth(24.0);
        addImg.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImg.setPickOnBounds(true);
        addImg.setPreserveRatio(true);
        Tooltip.install(addImg, new Tooltip("Hinzufügen"));

        return addImg;
    }

    public ImageView editButton() {
        ImageView editImg = new ImageView();
        editImg.setFitHeight(24.0);
        editImg.setFitWidth(24.0);
        editImg.setImage(new Image(getClass().getResourceAsStream("../../edit_black_24dp.png")));
        editImg.setPickOnBounds(true);
        editImg.setPreserveRatio(true);
        Tooltip.install(editImg, new Tooltip("Bearbeiten"));

        return editImg;
    }

    public ImageView deleteButton() {
        ImageView delImg = new ImageView();
        delImg.setFitHeight(24.0);
        delImg.setFitWidth(24.0);
        delImg.setImage(new Image(getClass().getResourceAsStream("../../del_black_24dp.png")));
        delImg.setPickOnBounds(true);
        delImg.setPreserveRatio(true);
        Tooltip.install(delImg, new Tooltip("Löschen"));

        return delImg;
    }

}
