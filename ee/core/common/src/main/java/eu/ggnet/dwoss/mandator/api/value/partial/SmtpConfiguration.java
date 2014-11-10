package eu.ggnet.dwoss.mandator.api.value.partial;

import java.io.Serializable;

import lombok.Data;

/**
 * Configuration for SMTP Operations.
 * <p/>
 * @author oliver.guenther
 */
@Data
public class SmtpConfiguration implements Serializable {

    private final String hostname;

    private final String smtpAuthenticationUser;

    private final String smtpAuthenticationPass;

    private final String charset;

    private final boolean useStartTls;
}
