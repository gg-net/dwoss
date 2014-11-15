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
package eu.ggnet.dwoss.report.returns;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.metawidget.inspector.annotation.UiHidden;

/**
 *
 * @author oliver.guenther
 */
public class Summary {

    public static final String PROP_REFERENCEPRICE = "referencePrice";

    public static final String PROP_PRICE = "price";

    public static final String PROP_PURCHASEPRICE = "purchasePrice";

    public static final String PROP_REFERENCEPRICEPERCENTAGE = "referencePricePercentage";

    public static final String PROP_MARGINPERCENTAGE = "marginPercentage";

    public static final String PROP_MARGIN = "margin";

    @UiHidden
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private String referencePrice;

    private String referencePricePercentage;

    private String price;

    private String purchasePrice;

    private String margin;

    private String marginPercentage;

    public void update(double referencePrice, double price, double purchasePrice, double margin) {
        setReferencePrice(toCurrency(referencePrice));
        setPrice(toCurrency(price));
        setPurchasePrice(toCurrency(purchasePrice));
        setMargin(toCurrency(margin));
        if ( price != 0 && referencePrice != 0 ) setReferencePricePercentage(toPercentage(price / referencePrice));
        if ( margin != 0 && purchasePrice != 0 ) setMarginPercentage(toPercentage(margin / purchasePrice));
    }

    /**
     * Get the value of referencePrice
     *
     * @return the value of referencePrice
     */
    public String getReferencePrice() {
        return referencePrice;
    }

    /**
     * Set the value of referencePrice
     *
     * @param referencePrice new value of referencePrice
     */
    public void setReferencePrice(String referencePrice) {
        String oldReferencePrice = this.referencePrice;
        this.referencePrice = referencePrice;
        propertyChangeSupport.firePropertyChange(PROP_REFERENCEPRICE, oldReferencePrice, referencePrice);
    }

    /**
     * Get the value of referencePricePercentage
     *
     * @return the value of referencePricePercentage
     */
    public String getReferencePricePercentage() {
        return referencePricePercentage;
    }

    /**
     * Set the value of referencePricePercentage
     *
     * @param referencePricePercentage new value of referencePricePercentage
     */
    public void setReferencePricePercentage(String referencePricePercentage) {
        String oldReferencePricePercentage = this.referencePricePercentage;
        this.referencePricePercentage = referencePricePercentage;
        propertyChangeSupport.firePropertyChange(PROP_REFERENCEPRICEPERCENTAGE, oldReferencePricePercentage, referencePricePercentage);
    }

    /**
     * Get the value of price
     *
     * @return the value of price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Set the value of price
     *
     * @param price new value of price
     */
    public void setPrice(String price) {
        String oldPrice = this.price;
        this.price = price;
        propertyChangeSupport.firePropertyChange(PROP_PRICE, oldPrice, price);
    }

    /**
     * Get the value of purchasePrice
     *
     * @return the value of purchasePrice
     */
    public String getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Set the value of purchasePrice
     *
     * @param purchasePrice new value of purchasePrice
     */
    public void setPurchasePrice(String purchasePrice) {
        String oldPurchasePrice = this.purchasePrice;
        this.purchasePrice = purchasePrice;
        propertyChangeSupport.firePropertyChange(PROP_PURCHASEPRICE, oldPurchasePrice, purchasePrice);
    }

    /**
     * Get the value of margin
     *
     * @return the value of margin
     */
    public String getMargin() {
        return margin;
    }

    /**
     * Set the value of margin
     *
     * @param margin new value of margin
     */
    public void setMargin(String margin) {
        String oldMargin = this.margin;
        this.margin = margin;
        propertyChangeSupport.firePropertyChange(PROP_MARGIN, oldMargin, margin);
    }

    /**
     * Get the value of marginPercentage
     *
     * @return the value of marginPercentage
     */
    public String getMarginPercentage() {
        return marginPercentage;
    }

    /**
     * Set the value of marginPercentage
     *
     * @param marginPercentage new value of marginPercentage
     */
    public void setMarginPercentage(String marginPercentage) {
        String oldMarginPercentage = this.marginPercentage;
        this.marginPercentage = marginPercentage;
        propertyChangeSupport.firePropertyChange(PROP_MARGINPERCENTAGE, oldMarginPercentage, marginPercentage);
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private String toCurrency(double currency) {
        NumberFormat format = new DecimalFormat(",##0.00");
        return format.format(currency) + " €";
    }

    private String toPercentage(double percentage) {
        if ( percentage == 0d || Double.isNaN(percentage) ) return "0 %";
        NumberFormat format = new DecimalFormat("#0.00");
        return format.format(percentage * 100) + " %";
    }
}
