package com.w2a.utilities;

import java.util.List;
import java.util.ArrayList;

/**
 * Value object representing an email request
 * Contains all necessary information to send an email
 */
public class MailRequest {
    private String from;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private String subject;
    private String htmlContent;
    private String textContent;
    private List<Attachment> attachments = new ArrayList<>();
    private String replyTo;
    private int priority = 3; // 1=high, 3=normal, 5=low

    // Constructors
    public MailRequest() {}

    public MailRequest(String from, List<String> to, String subject, String htmlContent) {
        this.from = from;
        this.to = to != null ? to : new ArrayList<>();
        this.subject = subject;
        this.htmlContent = htmlContent;
    }

    // Getters and Setters
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public List<String> getTo() { return to; }
    public void setTo(List<String> to2) { this.to = to2 != null ? to2 : new ArrayList<>(); }
    public void addTo(String email) { this.to.add(email); }

    public List<String> getCc() { return cc; }
    public void setCc(List<String> cc) { this.cc = cc != null ? cc : new ArrayList<>(); }
    public void addCc(String email) { this.cc.add(email); }

    public List<String> getBcc() { return bcc; }
    public void setBcc(List<String> bcc) { this.bcc = bcc != null ? bcc : new ArrayList<>(); }
    public void addBcc(String email) { this.bcc.add(email); }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments != null ? attachments : new ArrayList<>(); }
    public void addAttachment(Attachment attachment) { this.attachments.add(attachment); }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = Math.max(1, Math.min(5, priority)); }

    // Validation
    public boolean isValid() {
        return from != null && !from.trim().isEmpty() &&
               to != null && !to.isEmpty() &&
               subject != null && !subject.trim().isEmpty() &&
               (htmlContent != null || textContent != null);
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (from == null || from.trim().isEmpty()) {
            errors.append("From address is required. ");
        }
        if (to == null || to.isEmpty()) {
            errors.append("At least one recipient is required. ");
        }
        if (subject == null || subject.trim().isEmpty()) {
            errors.append("Subject is required. ");
        }
        if (htmlContent == null && textContent == null) {
            errors.append("Either HTML or text content is required. ");
        }
        return errors.toString().trim();
    }

    /**
     * Inner class representing an email attachment
     */
    public static class Attachment {
        private String filePath;
        private String fileName;
        private String mimeType;
        private byte[] content;

        public Attachment(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public Attachment(byte[] content, String fileName, String mimeType) {
            this.content = content;
            this.fileName = fileName;
            this.mimeType = mimeType;
        }

        // Getters and Setters
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }

        public byte[] getContent() { return content; }
        public void setContent(byte[] content) { this.content = content; }

        public boolean isFileBased() { return filePath != null && !filePath.trim().isEmpty(); }
        public boolean isContentBased() { return content != null && content.length > 0; }
    }
}
