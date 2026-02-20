package eu.ggnet.dwoss.redtapext.ee.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * Datenmodell für eine zu versendende E-Mail.
 */
public class EmailMessage {

    public record Attachment(String fileName, String contentType, byte[] data) {

        public long sizeBytes() {
            return data.length;
        }
    }

    private final String from;

    private final String to;

    private final List<String> cc;

    private final List<String> bcc;

    private final String subject;

    private final String body;

    private final boolean html;

    private final List<Attachment> attachments;

    private EmailMessage(Builder b) {
        this.from = b.from;
        this.to = b.to;
        this.cc = List.copyOf(b.cc);
        this.bcc = List.copyOf(b.bcc);
        this.subject = b.subject;
        this.body = b.body;
        this.html = b.html;
        this.attachments = List.copyOf(b.attachments);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public boolean isHtml() {
        return html;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String from;

        private String to;

        private final List<String> cc = new ArrayList<>();

        private final List<String> bcc = new ArrayList<>();

        private String subject;

        private String body;

        private boolean html = false;

        private final List<Attachment> attachments = new ArrayList<>();

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc.addAll(cc);
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc.addAll(bcc);
            return this;
        }

        public Builder subject(String s) {
            this.subject = s;
            return this;
        }

        public Builder body(String b) {
            this.body = b;
            return this;
        }

        public Builder html(boolean h) {
            this.html = h;
            return this;
        }

        public Builder attachment(String name, String ct, byte[] data) {
            this.attachments.add(new Attachment(name, ct, data));
            return this;
        }

        public EmailMessage build() {
            if ( from == null || from.isBlank() ) throw new IllegalStateException("'from' fehlt");
            if ( to == null || to.isBlank() ) throw new IllegalStateException("'to' fehlt");
            if ( subject == null || subject.isBlank() ) throw new IllegalStateException("'subject' fehlt");
            if ( body == null || body.isBlank() ) throw new IllegalStateException("'body' fehlt");
            return new EmailMessage(this);
        }
    }
}
