package eu.ggnet.dwoss.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 *
 * @author oliver.guenther
 */
public class MailTo {

    private final String to;

    private String subject;

    private String body;

    public MailTo(String to) {
        this.to = to;
    }

    public MailTo setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailTo setBody(String body) {
        this.body = body;
        return this;
    }

    public URI toUri() {
        try {
            return new URI("mailto:" + to + "?subject=" + URLEncoder.encode(subject, "UTF-8").replaceAll("\\+", "%20")
                    + "&body=" + URLEncoder.encode(body, "UTF-8").replaceAll("\\+", "%20"));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
