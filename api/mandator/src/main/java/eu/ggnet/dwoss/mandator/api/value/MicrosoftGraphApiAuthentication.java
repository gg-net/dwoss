package eu.ggnet.dwoss.mandator.api.value;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MicrosoftGraphApiAuthentication {

    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    
    /**
     * Microsoft Graph OAuth2 Authentication
     * 
     * @param tenantId
     * @param clientId
     * @param clientSecret 
     */
    public MicrosoftGraphApiAuthentication(String tenantId, String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tenantId = tenantId;
    }
    
    
    public String getClientId()     { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getTenantId()     { return tenantId; }
}
