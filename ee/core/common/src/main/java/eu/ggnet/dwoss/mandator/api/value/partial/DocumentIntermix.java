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
import java.net.URL;
import java.util.Map.Entry;
import java.util.*;

import org.metawidget.inspector.annotation.UiLarge;

import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.mandator.api.FreeDocumentTemplateParameter;
import eu.ggnet.dwoss.rules.DocumentType;

import lombok.*;

/**
 * Contains Details about a Document to intermix in the rendering process.
 * <p/>
 * @author oliver.guenther
 */
// TODO: We could make a Builder for this, but not now. For now I risk manipulation.
@ToString
@EqualsAndHashCode
public class DocumentIntermix implements Serializable {

    private final UrlLocation defaultDocumentTemplate;

    @Getter
    @Setter
    @UiLarge
    private String footer;

    private final Map<DocumentViewType, UrlLocation> viewTypeDocumentTemplates = new HashMap<>();

    private final Map<FreeDocumentTemplateParameter, String> defaultTexts = new HashMap<>();

    private final Map<DocumentViewType, Map<FreeDocumentTemplateParameter, String>> viewTypeTexts = new HashMap<>();

    private final Map<DocumentType, Map<FreeDocumentTemplateParameter, String>> documentTypeTexts = new HashMap<>();

    public String toMultiLine() {
        final StringBuilder sb = new StringBuilder("DocumentIntermix\n");
        sb.append("- defaultDocumentTemplate=").append(defaultDocumentTemplate).append("\n");
        sb.append("- footer=").append(footer).append("\n");

        sb.append("- viewTypeDocumentTemplates.size()=").append(viewTypeDocumentTemplates.size()).append("\n");
        for (Entry<DocumentViewType, UrlLocation> e : viewTypeDocumentTemplates.entrySet()) {
            sb.append("  - ").append(e.getKey()).append(":").append(e.getValue()).append("\n");
        }

        sb.append("- defaultTexts.size()=").append(defaultTexts.size()).append("\n");
        for (Entry<FreeDocumentTemplateParameter, String> e : defaultTexts.entrySet()) {
            sb.append("  - ").append(e.getKey()).append(":").append(e.getValue()).append("\n");
        }

        sb.append("- viewTypeTexts.size()=").append(viewTypeTexts.size()).append("\n");
        for (Entry<DocumentViewType, Map<FreeDocumentTemplateParameter, String>> e : viewTypeTexts.entrySet()) {
            sb.append("  - ").append(e.getKey()).append(":");
            if ( e.getValue() == null ) {
                sb.append("null\n");
            } else {
                sb.append("size()=").append(e.getValue().size());
                for (Entry<FreeDocumentTemplateParameter, String> f : e.getValue().entrySet()) {
                    sb.append("    - ").append(f.getKey()).append(":").append(f.getValue()).append("\n");
                }
            }
        }

        sb.append("- documentTypeTexts.size()=").append(documentTypeTexts.size()).append("\n");
        for (Entry<DocumentType, Map<FreeDocumentTemplateParameter, String>> e : documentTypeTexts.entrySet()) {
            sb.append("  - ").append(e.getKey()).append(":");
            if ( e.getValue() == null ) {
                sb.append("null\n");
            } else {
                sb.append("size()=").append(e.getValue().size());
                for (Entry<FreeDocumentTemplateParameter, String> f : e.getValue().entrySet()) {
                    sb.append("    - ").append(f.getKey()).append(":").append(f.getValue()).append("\n");
                }
            }
        }

        return sb.toString();
    }

    public String toHtml() {
        final StringBuilder sb = new StringBuilder("<p>");
        sb.append("<u>DefaultDocumentTemplate</u>: ").append(defaultDocumentTemplate).append("<br />");
        sb.append("<u>Footer</u>: ").append(footer).append("<br />");

        sb.append("<u>ViewTypeDocumentTemplates(").append(viewTypeDocumentTemplates.size()).append(")</u>:<ul>");
        for (Entry<DocumentViewType, UrlLocation> e : viewTypeDocumentTemplates.entrySet()) {
            sb.append("<li>").append(e.getKey()).append(":").append(e.getValue()).append("</li>");
        }
        sb.append("</ul>");
        sb.append("<u>DefaultTexts(").append(defaultTexts.size()).append(")</u>:<ul>");
        for (Entry<FreeDocumentTemplateParameter, String> e : defaultTexts.entrySet()) {
            sb.append("<li>").append(e.getKey()).append(":").append(e.getValue()).append("</li>");
        }
        sb.append("</ul>");
        sb.append("<u>ViewTypeTexts(").append(viewTypeTexts.size()).append(")</u>:<ul>");
        for (Entry<DocumentViewType, Map<FreeDocumentTemplateParameter, String>> e : viewTypeTexts.entrySet()) {
            sb.append("<li>").append(e.getKey()).append(":");
            if ( e.getValue() == null ) {
                sb.append("null");
            } else {
                sb.append("size(").append(e.getValue().size()).append(")<ul>");
                for (Entry<FreeDocumentTemplateParameter, String> f : e.getValue().entrySet()) {
                    sb.append("<li>").append(f.getKey()).append(":").append(f.getValue()).append("</li>>");
                }
                sb.append("</ul>");
            }
            sb.append("</li>");
        }
        sb.append("</ul>");

        sb.append("<u>DocumentTypeTexts(").append(documentTypeTexts.size()).append(")</u>:<ul>");
        for (Entry<DocumentType, Map<FreeDocumentTemplateParameter, String>> e : documentTypeTexts.entrySet()) {
            sb.append("<li>").append(e.getKey()).append(":");
            if ( e.getValue() == null ) {
                sb.append("null");
            } else {
                sb.append("size(").append(e.getValue().size()).append(")<ul>");
                for (Entry<FreeDocumentTemplateParameter, String> f : e.getValue().entrySet()) {
                    sb.append("<li>").append(f.getKey()).append(":").append(f.getValue()).append("</li>");
                }
                sb.append("</ul>");
            }
            sb.append("</li>");
        }
        sb.append("</ul></p>");
        return sb.toString();
    }

    public DocumentIntermix(UrlLocation defaultDocumentTemplate) {
        this.defaultDocumentTemplate = defaultDocumentTemplate;
    }

    public DocumentIntermix add(DocumentViewType viewType, UrlLocation url) {
        viewTypeDocumentTemplates.put(viewType, Objects.requireNonNull(url, "UrlLocation on add must not be null"));
        return this;
    }

    public DocumentIntermix add(FreeDocumentTemplateParameter parameter, String value) {
        defaultTexts.put(parameter, value);
        return this;
    }

    public DocumentIntermix add(FreeDocumentTemplateParameter parameter, DocumentViewType viewType, String value) {
        if ( !viewTypeTexts.containsKey(viewType) )
            viewTypeTexts.put(viewType, new EnumMap<>(FreeDocumentTemplateParameter.class));
        viewTypeTexts.get(viewType).put(parameter, value);
        return this;
    }

    public DocumentIntermix add(FreeDocumentTemplateParameter parameter, DocumentType documentType, String value) {
        if ( !documentTypeTexts.containsKey(documentType) )
            documentTypeTexts.put(documentType, new EnumMap<>(FreeDocumentTemplateParameter.class));
        documentTypeTexts.get(documentType).put(parameter, value);
        return this;
    }

    /**
     * Returns a the default document template or a specialized on for the viewtype.
     * <p/>
     * @param viewType the viewtype, if null the default document template will be returned.
     * @return a document template, may be null.
     */
    public URL getTemplate(final DocumentViewType viewType) {
        final URL defaultTemplate = defaultDocumentTemplate != null ? defaultDocumentTemplate.toURL() : null;
        if ( viewTypeDocumentTemplates == null ) return defaultTemplate;
        if ( !viewTypeDocumentTemplates.containsKey(viewType) ) return defaultTemplate;
        return viewTypeDocumentTemplates.get(viewType).toURL();
    }

    /**
     * Returns a FreeText based on the parameters.
     * <p/>
     * @param parameter the parameter.
     * @param viewType  an optional view type.
     * @param type      an optional document type.
     * @return a String for the parameter, never null.
     */
    public String getFreeTexts(FreeDocumentTemplateParameter parameter, DocumentViewType viewType, DocumentType type) {
        if ( parameter == null ) return "";
        if ( viewType != null
                && viewType != DocumentViewType.DEFAULT
                && viewTypeTexts.containsKey(viewType)
                && viewTypeTexts.get(viewType).containsKey(parameter) ) {
            return viewTypeTexts.get(viewType).get(parameter);
        }
        if ( documentTypeTexts.containsKey(type)
                && documentTypeTexts.get(type).containsKey(parameter) ) {
            return documentTypeTexts.get(type).get(parameter);
        }
        if ( defaultTexts.containsKey(parameter) ) {
            return defaultTexts.get(parameter);
        }
        return "";
    }
}
