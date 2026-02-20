package eu.ggnet.dwoss.redtapext.ee.mail;

import eu.ggnet.dwoss.mandator.api.value.MicrosoftGraphApiAuthentication;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holt und cached OAuth2-Tokens vom Microsoft Identity Platform Endpunkt.
 * <p>
 * Verwendet den Client Credentials Flow:
 * POST https://login.microsoftonline.com/{tenantId}/oauth2/v2.0/token
 * <p>
 * Nur Java-Bordmittel: java.net.http.HttpClient (Java 11+) + Jakarta JSON-P.
 */
@ApplicationScoped
public class TokenService {

    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private static final String TOKEN_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";

    private static final String SCOPE = "https://graph.microsoft.com/.default";

    @Inject
    private MicrosoftGraphApiAuthentication config;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Einfaches Token-Caching: Token + Ablaufzeit
    private volatile String cachedToken;

    private volatile Instant tokenExpiry = Instant.EPOCH;

    /**
     * Gibt ein gültiges Access Token zurück.
     * Holt automatisch ein neues, wenn das aktuelle abgelaufen ist.
     */
    public String getAccessToken() {
        // 60 Sekunden Puffer vor Ablauf
        if ( cachedToken != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60)) ) {
            return cachedToken;
        }
        return fetchNewToken();
    }

    private synchronized String fetchNewToken() {
        // Double-checked nach dem synchronized-Block
        if ( cachedToken != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60)) ) {
            return cachedToken;
        }

        String url = String.format(TOKEN_URL, config.getTenantId());
        String body = "grant_type=client_credentials"
                + "&client_id=" + urlEncode(config.getClientId())
                + "&client_secret=" + urlEncode(config.getClientSecret())
                + "&scope=" + urlEncode(SCOPE);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<InputStream> response
                    = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if ( response.statusCode() != 200 ) {
                String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                throw new RuntimeException(
                        "Token-Anfrage fehlgeschlagen (HTTP " + response.statusCode() + "): " + errorBody);
            }

            JsonObject json = Json.createReader(response.body()).readObject();

            this.cachedToken = json.getString("access_token");
            int expiresIn = json.getInt("expires_in", 3600);
            this.tokenExpiry = Instant.now().plusSeconds(expiresIn);

            LOG.info("OAuth2-Token erfolgreich geholt, gültig für {}s",expiresIn);
            return cachedToken;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Holen des OAuth2-Tokens: " + e.getMessage(), e);
        }
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
