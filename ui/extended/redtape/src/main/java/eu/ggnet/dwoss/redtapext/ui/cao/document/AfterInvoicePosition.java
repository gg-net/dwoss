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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import eu.ggnet.dwoss.redtape.entity.Position;

/**
 *
 * @author pascal.perau
 */
public class AfterInvoicePosition {

    private boolean fullCredit;

    private boolean partialCredit;

    private boolean participate;

    final private double originalPrice;

    private Position position;

    public AfterInvoicePosition(boolean fullCredit, boolean partialCredit, Position position) {
        this.fullCredit = fullCredit;
        this.partialCredit = partialCredit;
        this.originalPrice = position.getPrice() * position.getAmount();
        this.position = modifyToAfterInvoicePosition(position);
    }

    public AfterInvoicePosition(Position position) {
        this(false, false, position);
    }

    /**
     * Get the value of fullCredit
     *
     * @return the value of fullCredit
     */
    public boolean isFullCredit() {
        return fullCredit;
    }

    /**
     * Set the value of fullCredit
     *
     * @param fullCredit new value of fullCredit
     */
    public void setFullCredit(boolean fullCredit) {
        this.fullCredit = fullCredit;
    }

    /**
     * Get the value of partialCredit
     *
     * @return the value of partialCredit
     */
    public boolean isPartialCredit() {
        return partialCredit;
    }

    /**
     * Set the value of partialCredit
     *
     * @param partialCredit new value of partialCredit
     */
    public void setPartialCredit(boolean partialCredit) {
        this.partialCredit = partialCredit;
    }

    /**
     * Get the value of participateCredit
     *
     * @return the value of participateCredit
     */
    public boolean isParticipant() {
        return participate;
    }

    /**
     * Set the value of participateCredit
     *
     * @param participate new value of participateCredit
     */
    public void setParticipate(boolean participate) {
        this.participate = participate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    private Position modifyToAfterInvoicePosition(Position p) {
        p.setPrice(p.getPrice() * p.getAmount());
        p.setAmount(1);
        return p;
    }

    @Override
    public String toString() {
        return "AfterInvoicePosition{" + "fullCredit=" + fullCredit + ", partialCredit=" + partialCredit + ", participateCredit=" + participate + ", position=" + position + '}';
    }
}
