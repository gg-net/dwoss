package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;
import java.net.URL;

import lombok.Value;
import lombok.experimental.Builder;

/**
 * Valueholder for attachments in {@link Mandator} mail attachments.
 * <p>
 * @author pascal.perau
 */
@Value
@Builder
public class MandatorMailAttachment implements Serializable {

    private URL attachmentData;

    private String attachmentName;

    private String attachmentDescription;

}
