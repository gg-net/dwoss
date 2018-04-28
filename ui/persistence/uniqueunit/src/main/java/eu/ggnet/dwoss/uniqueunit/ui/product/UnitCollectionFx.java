/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.util.*;

import javafx.beans.property.*;
import javafx.collections.*;

import eu.ggnet.dwoss.common.api.values.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;

import lombok.*;

/**
 * The Fx variant of a UnitCollection.
 *
 * @author jens.papenhagen
 */
@ToString
public class UnitCollectionFx {
    
    @Data
    @AllArgsConstructor
    public static class Price {
        
        private PriceType type;
        
        private double value;
        
    }
    
    @Getter
    @Setter
    private long id = 0;

    private final StringProperty nameExtensionProperty = new SimpleStringProperty(this, "nameExtension");

    private final StringProperty descriptionExtensionProperty = new SimpleStringProperty(this, "descriptionExtension");

    private final StringProperty partNoExtensionProperty= new SimpleStringProperty(this, "partNoExtension");

    @Getter
    @Setter
    private List<PicoUnit> units = new ArrayList<>();

    private final ObservableList<Price> prices = FXCollections.observableArrayList();
    
    private final ObjectProperty<SalesChannel> salesChannelProperty = new SimpleObjectProperty<>(this, "salesChannel");

    public UnitCollectionFx() {
    }

    public final void setNameExtension(String value) {
        nameExtensionProperty.set(value);
    }

    public final String getNameExtension() {
        return nameExtensionProperty.get();
    }

    public final StringProperty nameExtensionProperty() {
        return nameExtensionProperty;
    }

    public final void setDescriptionExtension(String value) {
        descriptionExtensionProperty.set(value);
    }

    public final String getDescriptionExtension() {
        return descriptionExtensionProperty.get();
    }

    public final StringProperty descriptionExtensionProperty() {
        return descriptionExtensionProperty;
    }


    public final void setPartNoExtension(String value) {
        partNoExtensionProperty.set(value);
    }

    public final String getPartNoExtension() {
        return partNoExtensionProperty.get();
    }

    public final StringProperty partNoExtensionProperty() {
        return partNoExtensionProperty;
    }

    public SalesChannel getSalesChannel() {
        return salesChannelProperty.getValue();
    }

    public final void setSalesChannel(SalesChannel value) {
        salesChannelProperty.set(value);
    }

    public ObjectProperty<SalesChannel> salesChannelProperty() {
        return salesChannelProperty;
    }
 
    public ObservableList<Price> getPrices() {
        return prices;
    }
    
}
