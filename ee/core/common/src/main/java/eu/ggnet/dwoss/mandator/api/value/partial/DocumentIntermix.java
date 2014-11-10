package eu.ggnet.dwoss.mandator.api.value.partial;

import eu.ggnet.dwoss.mandator.api.FreeDocumentTemplateParameter;

import java.io.Serializable;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.metawidget.inspector.annotation.UiLarge;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Contains Details about a Document to intermix in the rendering process.
 * <p/>
 * @author oliver.guenther
 */
// TODO: We could make a Builder for this, but not now. For now I risk manipulation.
@Getter
@ToString
@EqualsAndHashCode
public class DocumentIntermix implements Serializable {

    private final URL defaultDocumentTemplate;

    private final Map<DocumentViewType, URL> viewTypeDocumentTemplates = new HashMap<>();

    @Getter
    @Setter
    @UiLarge
    private String footer;

    private final Map<FreeDocumentTemplateParameter, String> defaultTexts = new HashMap<>();

    private final Map<DocumentViewType, Map<FreeDocumentTemplateParameter, String>> viewTypeTexts = new HashMap<>();

    private final Map<DocumentType, Map<FreeDocumentTemplateParameter, String>> documentTypeTexts = new HashMap<>();

    public DocumentIntermix(URL defaultDocumentTemplate) {
        this.defaultDocumentTemplate = defaultDocumentTemplate;
    }

    public DocumentIntermix add(DocumentViewType viewType, URL url) {
        viewTypeDocumentTemplates.put(viewType, url);
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
     * @return a document template.
     */
    public URL getTemplate(DocumentViewType viewType) {
        if ( viewTypeDocumentTemplates == null ) return defaultDocumentTemplate;
        if ( !viewTypeDocumentTemplates.containsKey(viewType) ) return defaultDocumentTemplate;
        return viewTypeDocumentTemplates.get(viewType);
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
