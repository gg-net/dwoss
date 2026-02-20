package eu.ggnet.dwoss.redtapext.ee.mail;

import eu.ggnet.dwoss.mandator.api.value.MicrosoftGraphApiAuthentication;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Versendet E-Mails über die Microsoft Graph REST API.
 * <p>
 * Endpunkt: POST https://graph.microsoft.com/v1.0/users/{sender}/sendMail
 * <p>
 * Kein Microsoft SDK – nur Jakarta JSON-P zum Bauen des JSON-Payloads
 * und java.net.http.HttpClient für den HTTP-Aufruf.
 * <p>
 * Graph API Dokumentation:
 * https://learn.microsoft.com/en-us/graph/api/user-sendmail
 */
@ApplicationScoped
public class GraphEmailService {

    private static final Logger L = LoggerFactory.getLogger(GraphEmailService.class);

    private static final String GRAPH_URL = "https://graph.microsoft.com/v1.0/users/%s/sendMail";

    private static final long MAX_BYTES = 3L * 1024 * 1024; // 3 MB Graph-Limit

    @Inject
    private TokenService tokenService;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Sendet die E-Mail.
     *
     * @param msg the messge to be send.
     * @throws eu.ggnet.dwoss.redtapext.ee.mail.EmailException if something goes wrong.
     */
    public void sendEmail(EmailMessage msg) throws EmailException {
        validateAttachments(msg);

        String json = buildJsonPayload(msg);
        String token = tokenService.getAccessToken();
        String url = String.format(GRAPH_URL, urlEncode(msg.getFrom()));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<InputStream> response
                    = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            int status = response.statusCode();
            if ( status == 202 ) {
                // 202 Accepted = Erfolg (Graph sendMail gibt keinen Body zurück)
                L.info("E-Mail erfolgreich gesendet an:{},cc:{},bcc:{} ", msg.getTo(), msg.getCc(), msg.getBcc());
            } else {
                String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                L.error("Microsoft Graph API Fehler (HTTP " + status + "): " + extractErrorMessage(errorBody));
                throw new EmailException("Microsoft Graph API Fehler (HTTP " + status + "): " + extractErrorMessage(errorBody));
            }
        } catch (RuntimeException | IOException | InterruptedException e) {
            L.error("Exception during Microsoft Graph API", e);
            throw new EmailException("Exception during Microsoft Graph API processing:" + e.getClass().getName() + ":" + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // JSON-Payload Aufbau mit Jakarta JSON-P (javax.json / jakarta.json)
    // -------------------------------------------------------------------------
    private String buildJsonPayload(EmailMessage msg) {
        JsonObjectBuilder messageBuilder = Json.createObjectBuilder()
                .add("subject", msg.getSubject())
                .add("body", Json.createObjectBuilder()
                        .add("contentType", msg.isHtml() ? "HTML" : "Text")
                        .add("content", msg.getBody()))
                .add("toRecipients", buildRecipientArray(msg.getTo()));

        if ( !msg.getCc().isEmpty() ) {
            JsonArrayBuilder ccArray = Json.createArrayBuilder();
            msg.getCc().forEach(addr -> ccArray.add(recipientObject(addr)));
            messageBuilder.add("ccRecipients", ccArray);
        }

        if ( !msg.getBcc().isEmpty() ) {
            JsonArrayBuilder bccArray = Json.createArrayBuilder();
            msg.getBcc().forEach(addr -> bccArray.add(recipientObject(addr)));
            messageBuilder.add("bccRecipients", bccArray);
        }

        if ( msg.hasAttachments() ) {
            JsonArrayBuilder attachArray = Json.createArrayBuilder();
            for (EmailMessage.Attachment att : msg.getAttachments()) {
                attachArray.add(Json.createObjectBuilder()
                        .add("@odata.type", "#microsoft.graph.fileAttachment")
                        .add("name", att.fileName())
                        .add("contentType", att.contentType())
                        .add("contentBytes", Base64.getEncoder().encodeToString(att.data())));
            }
            messageBuilder.add("attachments", attachArray);
        }

        JsonObject payload = Json.createObjectBuilder()
                .add("message", messageBuilder)
                .add("saveToSentItems", true)
                .build();

        return payload.toString();
    }

    private JsonArrayBuilder buildRecipientArray(String... addresses) {
        JsonArrayBuilder arr = Json.createArrayBuilder();
        for (String addr : addresses) arr.add(recipientObject(addr));
        return arr;
    }

    private JsonObject recipientObject(String email) {
        return Json.createObjectBuilder()
                .add("emailAddress", Json.createObjectBuilder()
                        .add("address", email.trim()))
                .build();
    }

    // -------------------------------------------------------------------------
    // Hilfsmethoden
    // -------------------------------------------------------------------------
    private void validateAttachments(EmailMessage msg) {
        for (EmailMessage.Attachment att : msg.getAttachments()) {
            if ( att.sizeBytes() > MAX_BYTES ) {
                throw new IllegalArgumentException(
                        "Anhang '" + att.fileName() + "' ist zu groß ("
                        + (att.sizeBytes() / 1024 / 1024) + " MB). Maximum: 3 MB.");
            }
        }
    }

    /**
     * Extrahiert die Fehlermeldung aus dem Graph-API-Fehler-JSON.
     * Fallback auf den Rohtext, wenn Parsing fehlschlägt.
     */
    private String extractErrorMessage(String json) {
        try {
            JsonObject root = Json.createReader(
                    new java.io.StringReader(json)).readObject();
            return root.getJsonObject("error").getString("message", json);
        } catch (Exception e) {
            return json;
        }
    }

    private String urlEncode(String value) {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
