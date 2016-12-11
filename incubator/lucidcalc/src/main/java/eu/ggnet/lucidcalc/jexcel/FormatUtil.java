/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc.jexcel;

import java.awt.Color;

import jxl.biff.DisplayFormat;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;

/**
 * FormatUtil for the JExcelOffice. This utility class must be handled special. Because the JExcelApi keeps some activities static, it must be ensured,
 * that every object created for a Workbook will be dropped and recreated for another workbook. To ensure this, an instance of the utility class is needed.
 */
public class FormatUtil {

    private DateFormat shortDate;

    private NumberFormat currencyEuro;

    public Colour discover(Color color) {
        for (Colour colour : Colour.getAllColours()) {
            if ( colour.getDefaultRGB().getBlue() == color.getBlue()
                    && colour.getDefaultRGB().getRed() == color.getRed()
                    && colour.getDefaultRGB().getGreen() == color.getGreen() ) {
                return colour;
            }
        }
        return Colour.UNKNOWN;
    }

    public Alignment discover(eu.ggnet.lucidcalc.CFormat.HorizontalAlignment horizontalAlignment) {
        switch (horizontalAlignment) {
            case CENTER:
                return Alignment.CENTRE;
            case LEFT:
                return Alignment.LEFT;
            case RIGHT:
                return Alignment.RIGHT;
        }
        return Alignment.GENERAL;
    }

    public VerticalAlignment discover(eu.ggnet.lucidcalc.CFormat.VerticalAlignment verticalAlignment) {
        switch (verticalAlignment) {
            case TOP:
                return VerticalAlignment.TOP;
            case MIDDLE:
                return VerticalAlignment.CENTRE;
            case BOTTOM:
                return VerticalAlignment.BOTTOM;
        }
        return VerticalAlignment.JUSTIFY;
    }

    public DisplayFormat discover(eu.ggnet.lucidcalc.CFormat.Representation representation) {
        switch (representation) {
            case DEFAULT:
                return NumberFormats.DEFAULT;
            case TEXT:
                return NumberFormats.TEXT;
            case PERCENT_INTEGER:
                return NumberFormats.PERCENT_INTEGER;
            case PERCENT_FLOAT:
                return NumberFormats.PERCENT_FLOAT;
            case SHORT_DATE:
                if ( shortDate == null ) shortDate = new DateFormat("dd.MM.yy");
                return shortDate;
            case CURRENCY_EURO:
                if ( currencyEuro == null ) currencyEuro = new jxl.write.NumberFormat("#,#00.00 \u20AC", jxl.write.NumberFormat.COMPLEX_FORMAT);
                return currencyEuro;
        }
        return NumberFormats.DEFAULT;
    }

    public BorderLineStyle discover(eu.ggnet.lucidcalc.CBorder.LineStyle lineStyle) {
        if ( lineStyle == null ) return BorderLineStyle.NONE;
        // This just works, because I use the same order.
        return BorderLineStyle.getStyle(lineStyle.ordinal());
    }
}
