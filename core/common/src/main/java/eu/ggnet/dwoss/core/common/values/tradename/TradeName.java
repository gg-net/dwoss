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
package eu.ggnet.dwoss.core.common.values.tradename;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Identifies all Partners including ourselves.
 * This is a candiate to be extracted into a service, but as long as these are less than 40, don't think about it.
 *
 * @author oliver.guenther
 */
public enum TradeName {

    ONESELF("Your own Company"),
    ACER("Acer") {
        @Override
        public TradeName getManufacturer() {
            return ACER;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(ACER, PACKARD_BELL, EMACHINES, GATEWAY);
        }

        @Override
        public PartNoSupport getPartNoSupport() {
            return new AcerPartNoSupport();
        }

    },
    PACKARD_BELL("Packard Bell") {
        @Override
        public TradeName getManufacturer() {
            return ACER;
        }
    },
    EMACHINES("eMachines") {
        @Override
        public TradeName getManufacturer() {
            return ACER;
        }
    },
    GATEWAY("Gateway") {
        @Override
        public TradeName getManufacturer() {
            return ACER;
        }
    },
    OTTO("Otto") {

        @Override
        public PartNoSupport getPartNoSupport() {
            return new OttoPartNoSupport();
        }

    },
    NULL_NULL_4("004"),
    APPLE("Apple") {
        @Override
        public TradeName getManufacturer() {
            return APPLE;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(APPLE);
        }

        @Override
        public PartNoSupport getPartNoSupport() {
            return new ApplePartNoSupport();
        }
    },
    HP("Hewlett-Packard") {
        @Override
        public TradeName getManufacturer() {
            return HP;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(HP);
        }

        @Override
        public PartNoSupport getPartNoSupport() {
            return new HpPartNoSupport();
        }
    },
    FUJITSU("Fujitsu") {
        @Override
        public TradeName getManufacturer() {
            return FUJITSU;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(FUJITSU);
        }
    },
    DELL("Dell") {
        @Override
        public TradeName getManufacturer() {
            return DELL;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(DELL);
        }
    },
    AMAZON("Amazon"),
    EBAY("eBay"),
    SAMSUNG("Samsung") {
        @Override
        public TradeName getManufacturer() {
            return SAMSUNG;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(SAMSUNG);
        }
    },
    LG("LG Electronics") {
        @Override
        public TradeName getManufacturer() {
            return LG;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(LG);
        }
    },
    ALSO("Also"),
    INGRAM_MICRO("Ingram Mirco"),
    LENOVO("Lenovo") {
        @Override
        public TradeName getManufacturer() {
            return LENOVO;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(LENOVO);
        }

        @Override
        public PartNoSupport getPartNoSupport() {
            return new LenovoPartNoSupport();
        }
    }, SONOS("Sonos") {
        @Override
        public TradeName getManufacturer() {
            return SONOS;
        }

        @Override
        public Set<TradeName> getBrands() {
            return EnumSet.of(SONOS);
        }
    };

    private final String description;

    /**
     * Rule: if we are a brand, the Manufacturer is not null.
     */
    private final TradeName manufacturer = null;

    /**
     * Rule: If we are a Manufacturer, brands is not Empty (never null).
     */
    private final Set<TradeName> brands = Collections.emptySet();

    private TradeName(String name) {
        this.description = name;
    }

    /**
     *
     * @return
     * @deprecated use getDescription().
     */
    @Deprecated
    public String getName() {
        return description;
    }

    public String getDescription() {
        return description;
    }

    public TradeName getManufacturer() {
        return manufacturer;
    }

    public Set<TradeName> getBrands() {
        return brands;
    }

    /**
     * Rule: if we are a brand, the Manufacturer is not null.
     * <p>
     * @return true if a brand.
     */
    public boolean isBrand() {
        return getManufacturer() != null;
    }

    /**
     * Applied Rule: If we are a manufacturer, brands is not Empty (never null).
     * <p>
     * @return true if is a manufacturer.
     */
    public boolean isManufacturer() {
        return !getBrands().isEmpty();
    }

    /**
     * An optional part no supporter.
     * <p>
     * @return an optional part no supporter or null.
     */
    public PartNoSupport getPartNoSupport() {
        return null;
    }

    /**
     * Returns all manufacturers.
     * <p>
     * @return all manufacturers.
     */
    public static Set<TradeName> getManufacturers() {
        return Arrays.stream(values()).filter(TradeName::isManufacturer).collect(toSet());
    }
}
