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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TaxType;

import lombok.Value;

import static eu.ggnet.dwoss.rules.PositionType.SERVICE;
import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.rules.TaxType.GENERAL_SALES_TAX_DE_SINCE_2007;
import static eu.ggnet.dwoss.rules.TaxType.REVERSE_CHARGE;

/**
 * PostLedger (Fibu Buchungskonto) engine.
 *
 * @author Bastian Venz
 * @author Oliver Guenther
 */
@Value
public class PostLedger implements Serializable {

    private final static Logger L = LoggerFactory.getLogger(PostLedger.class);

    public static class Loader implements Serializable {

        private final Set<PositionType> positionTypes = EnumSet.noneOf(PositionType.class);

        private final Set<TaxType> taxTypes = EnumSet.noneOf(TaxType.class);

        private Ledger primaryLedger;

        private final List<Ledger> alternativeLedgers = new ArrayList<>();

        public Loader positionTypes(PositionType... types) {
            if ( types == null ) return this;
            positionTypes.addAll(Arrays.asList(types));
            return this;
        }

        public Loader taxTypes(TaxType... types) {
            if ( types == null ) return this;
            taxTypes.addAll(Arrays.asList(types));
            return this;
        }

        public Loader primaryLedger(int value, String description) {
            primaryLedger = new Ledger(value, description);
            alternativeLedgers.add(primaryLedger);
            return this;
        }

        public Loader primaryLedger(Ledger ledger) {
            Objects.requireNonNull(ledger, "Ledger is null, not allowed");
            primaryLedger = ledger;
            alternativeLedgers.add(ledger);
            return this;
        }

        public Loader alternativeLedger(int value, String description) {
            alternativeLedgers.add(new Ledger(value, description));
            return this;
        }

        public Loader alternativeLedger(Ledger ledger) {
            Objects.requireNonNull(ledger, "Ledger is null, not allowed");
            alternativeLedgers.add(ledger);
            return this;
        }

    }

    public static Loader add() {
        return new Loader();
    }

    @Value
    public static class Storage implements Serializable {

        private final Ledger primaryLedger;

        private final List<Ledger> alternativeLedgers = new ArrayList<>();

        public Storage(Ledger primaryLedger) {
            this.primaryLedger = primaryLedger;
        }

        public Storage(Ledger primaryLedger, List<Ledger> alternativeLedgers) {
            this.primaryLedger = primaryLedger;
            this.alternativeLedgers.addAll(alternativeLedgers);
        }

    }


    private final Map<PositionType, Map<TaxType, Storage>> positionAndTaxStorage = new EnumMap<>(PositionType.class);

    public PostLedger(Loader... loaders) {
        if ( loaders == null ) {
            L.info("Loading {} without a loader, meaning it is empty");
            return;
        }
        for (Loader loader : loaders) {
            if ( loader.positionTypes.isEmpty() ) throw new IllegalArgumentException("Loader without PositionTypes not allowed");
            if ( loader.taxTypes.isEmpty() ) throw new IllegalArgumentException("Loader without TaxTypes not allowed");
            for (PositionType positionType : loader.positionTypes) {
                if ( positionAndTaxStorage.get(positionType) == null ) positionAndTaxStorage.put(positionType, new EnumMap<>(TaxType.class));
                Map<TaxType, Storage> taxStorage = positionAndTaxStorage.get(positionType);
                for (TaxType taxType : loader.taxTypes) {
                    if ( taxStorage.get(taxType) != null && loader.primaryLedger != null )
                        throw new IllegalArgumentException("Trying to overwrite primary ledger " + taxStorage.get(taxType).primaryLedger + " with " + loader.primaryLedger);
                    if ( taxStorage.get(taxType) == null ) taxStorage.put(taxType, new Storage(loader.primaryLedger, loader.alternativeLedgers));
                    else taxStorage.get(taxType).alternativeLedgers.addAll(loader.alternativeLedgers);
                }
            }
        }
    }

    /*
    Scenarien:
    1. Default -> bassiert nur auf position type
    2. Reverse Charge -> passiert durch externen eingriff. (Oder aufgrund der Geräte vieleicht irgendwan) -> taxType
    3. östereich, dhl -> kid, country of kid, extra eingriff -> taxType

     */
    public Optional<Ledger> get(PositionType positionType, TaxType taxType) {
        Objects.requireNonNull(positionType, "PositionType must not be null");
        Objects.requireNonNull(taxType, "TaxType must not be null");
        if ( positionAndTaxStorage.containsKey(positionType) && positionAndTaxStorage.get(positionType).containsKey(taxType) )
            return Optional.of(positionAndTaxStorage.get(positionType).get(taxType).primaryLedger);
        return Optional.empty();
    }

    public List<Ledger> getAlternatives(PositionType positionType, TaxType taxType) {
        Objects.requireNonNull(positionType, "PositionType must not be null");
        Objects.requireNonNull(taxType, "TaxType must not be null");
        if ( positionAndTaxStorage.containsKey(positionType) && positionAndTaxStorage.get(positionType).containsKey(taxType) )
            return positionAndTaxStorage.get(positionType).get(taxType).alternativeLedgers;
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        PostLedger postLedger = new PostLedger(
                PostLedger.add()
                        .positionTypes(UNIT)
                        .taxTypes(REVERSE_CHARGE)
                        .primaryLedger(8401, "Acer Sonderposten Geräte"),
                PostLedger.add()
                        .positionTypes(UNIT)
                        .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                        .primaryLedger(8401, "Acer Sonderposten Geräte"),
                PostLedger.add()
                        .positionTypes(SERVICE)
                        .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                        .primaryLedger(8141, "Dienstleistung Store")
                        .alternativeLedger(8404, "Acer Reparatur Abrechnung")
                        .alternativeLedger(8406, "Versand")
        );

        System.out.println(postLedger);
    }

}
