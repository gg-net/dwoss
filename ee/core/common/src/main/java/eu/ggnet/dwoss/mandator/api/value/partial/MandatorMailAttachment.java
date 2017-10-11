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
package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;

import eu.ggnet.dwoss.mandator.api.value.Mandator;

import lombok.Builder;
import lombok.Value;

/**
 * Valueholder for attachments in {@link Mandator} mail attachments.
 * <p>
 * @author pascal.perau
 */
@Value
@Builder
public class MandatorMailAttachment implements Serializable {

    private UrlLocation attachmentData;

    private String attachmentName;

    private String attachmentDescription;

    /**
     * ToString HTML representation.
     *
     * @return HTML view of the MandatorMailAttachment.
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder("Attachment: ");
        sb.append("<b>");
        sb.append(attachmentName);
        sb.append("</b> &nbsp;");
        sb.append("<a href=\" ");
        sb.append(attachmentData.getLocation());
        sb.append(" \" title=\"");
        sb.append(attachmentDescription);
        sb.append("\" >");
        sb.append("Click here</a>");

        return sb.toString();
    }

    /**
     * toHtmlSingleLine HTML representation.
     *
     * @return HTML on a Singleline view of the MandatorMailAttachment.
     */
    public String toHtmlSingleLine() {
        StringBuilder sb = new StringBuilder("<p>");
        sb.append("<b>");
        sb.append(attachmentName);
        sb.append("</b> ");
        sb.append("<i>");
        sb.append(attachmentDescription);
        sb.append("</i> on: ");
        sb.append(attachmentData.getLocation());
        sb.append("</p>");

        return sb.toString();
    }

}
