/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.misc.op.movement;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A line for the movement reports
 */
@NoArgsConstructor
@Data
public class MovementLine {

    public static List<MovementLine> makeSamples() {
        List<MovementLine> result = new ArrayList<>();
        String lager1 = "Laden (Manhagener Allee)";
        String lager2 = "Lager (Strusbek)";
        String invoiceAddress = "Max Mustermann\n"
                + "Lange Straße 22\n"
                + "22221 Waldweld";
        String deliveryAddress = "Rote Florra e.V.\n"
                + "Max Mustermann\n"
                + "Sternschanze 123\n"
                + "21000 Hamburt\n";
        String c1 = "Ein langer\n"
                + "mehrzeiliger\n"
                + "Komentar.";
        MovementLine l = new MovementLine(1, "Eine Bemerkung", "SP231", "", invoiceAddress, deliveryAddress, "Vorkasse");
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire 7550 (Notebook)", "22001", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire X3100 (Aspire PC)", "43551", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer Veriton L640 (Commercial PC)", "99421", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "Acer TravelMate 1000", "10001", lager1, false));
        result.add(l);

        l = new MovementLine(1, c1, "DW00485", c1, invoiceAddress, deliveryAddress, "Nachnahme");
        l.addMovementSubline(new MovementSubline(1, "Acer Aspire 4810TG-12G32 (Notebook)", "20103", lager1, true));
        result.add(l);

        l = new MovementLine(2, "", "DW00351", "Fehler im Lager", invoiceAddress, deliveryAddress, "Vorkasse");
        l.addMovementSubline(new MovementSubline(1, "Packard Bell EasyNote 8922 (Notebook)", "148551", lager1, true));
        l.addMovementSubline(new MovementSubline(1, "eMachines E730 (Notebook)", "149521", lager2, false));
        result.add(l);

        return result;
    }

    private long customerId;

    private String customerComment;

    private String dossierIdentifier;

    private String invoiceAddress;

    private String deliveryAddress;

    private String comment;

    private String paymentMethod;

    private List<MovementSubline> movementSublines = new ArrayList<>();

    /**
     * All Parameter Constructor.
     *
     * @param customerId        the id of the customer
     * @param customerComment   the comment for the customer
     * @param dossierIdentifier the business transaction id
     * @param comment           the comment of the business transaction
     * @param invoiceAddress    the invoice address
     * @param deliveryAddress   the delivery address
     * @param paymentMethod     the payment method
     */
    public MovementLine(int customerId, String customerComment, String dossierIdentifier, String comment,
                        String invoiceAddress, String deliveryAddress, String paymentMethod) {
        this.customerId = customerId;
        this.customerComment = customerComment;
        this.dossierIdentifier = dossierIdentifier;
        this.invoiceAddress = invoiceAddress;
        this.deliveryAddress = deliveryAddress;
        this.comment = comment;
        this.paymentMethod = paymentMethod;
    }

    public void addMovementSubline(MovementSubline line) {
        movementSublines.add(line);
    }
}
