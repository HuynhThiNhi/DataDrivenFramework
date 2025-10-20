package com.w2a.utilities;

import com.w2a.base.TestBase;
import com.w2a.utilities.ExtentStepLogger;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * Refactored email service with modern practices
 * Supports HTML/text content, attachments, and proper error handling
 */
public class MonitoringMail {
    
    private final MailConfig config;
    private final Session session;
    
    /**
     * Constructor with MailConfig
     * @param config Email configuration
     * @throws MessagingException if session creation fails
     */
    public MonitoringMail(MailConfig config) throws MessagingException {
        if (config == null || !config.isValid()) {
            throw new IllegalArgumentException("Invalid mail configuration: " + 
                (config != null ? config.getValidationErrors() : "Config is null"));
        }
        this.config = config;
        this.session = createSession();
    }
    
    /**
     * Constructor using TestConfig (backward compatibility)
     * @throws MessagingException if session creation fails
     */
    public MonitoringMail() throws MessagingException {
        this(createConfigFromTestConfig());
    }
    
    /**
     * Send email using MailRequest
     * @param request Email request containing all necessary information
     * @throws MessagingException if sending fails
     */
    public void sendMail(MailRequest request) throws MessagingException {
        if (request == null || !request.isValid()) {
            throw new IllegalArgumentException("Invalid mail request: " + 
                (request != null ? request.getValidationErrors() : "Request is null"));
        }
        
        try {
            ExtentStepLogger.logStep("Preparing email: " + request.getSubject());
            
            MimeMessage message = createMessage(request);
            Transport.send(message);
            
            ExtentStepLogger.logPass("Email sent successfully to " + request.getTo().size() + " recipient(s)");
            TestBase.logInfo("Email sent successfully: " + request.getSubject());
            
        } catch (MessagingException e) {
            ExtentStepLogger.logFail("Failed to send email: " + e.getMessage());
            TestBase.logError("Email sending failed: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            ExtentStepLogger.logFail("Failed to attach file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Send email using legacy parameters (backward compatibility)
     * @param mailServer SMTP server host
     * @param from From address
     * @param to Array of recipient addresses
     * @param subject Email subject
     * @param messageBody HTML message body
     * @throws MessagingException if sending fails
     */
    public void sendMail(String mailServer, String from, String[] to, String subject, String messageBody) 
            throws MessagingException {
        
        if (to == null || to.length == 0) {
            throw new IllegalArgumentException("Recipients cannot be null or empty");
        }
        
        MailRequest request = new MailRequest();
        request.setFrom(from);
        for (String recipient : to) {
            request.addTo(recipient);
        }
        request.setSubject(subject);
        request.setHtmlContent(messageBody);
        
        sendMail(request);
    }
    
    /**
     * Create JavaMail session
     * @return Configured Session
     * @throws MessagingException if session creation fails
     */
    private Session createSession() throws MessagingException {
        Properties props = config.toProperties();
        Authenticator authenticator = new SMTPAuthenticator(config.getUsername(), config.getPassword());
        return Session.getInstance(props, authenticator);
    }
    
    /**
     * Create MimeMessage from MailRequest
     * @param request Email request
     * @return Configured MimeMessage
     * @throws MessagingException if message creation fails
     */
    private MimeMessage createMessage(MailRequest request) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(session);
        
        // Set headers
        message.addHeader("X-Priority", String.valueOf(request.getPriority()));
        message.setFrom(new InternetAddress(request.getFrom()));
        
        // Set recipients
        setRecipients(message, Message.RecipientType.TO, request.getTo());
        setRecipients(message, Message.RecipientType.CC, request.getCc());
        setRecipients(message, Message.RecipientType.BCC, request.getBcc());
        
        // Set reply-to if specified
        if (request.getReplyTo() != null && !request.getReplyTo().trim().isEmpty()) {
            message.setReplyTo(new InternetAddress[]{new InternetAddress(request.getReplyTo())});
        }
        
        // Set subject with proper encoding
        message.setSubject(request.getSubject());
        
        // Set content
        setMessageContent(message, request);
        
        return message;
    }
    
    /**
     * Set recipients for a message
     * @param message MimeMessage
     * @param type Recipient type (TO, CC, BCC)
     * @param recipients List of recipient addresses
     * @throws MessagingException if setting recipients fails
     */
    private void setRecipients(MimeMessage message, Message.RecipientType type, List<String> recipients) 
            throws MessagingException {
        if (recipients != null && !recipients.isEmpty()) {
            InternetAddress[] addresses = new InternetAddress[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                addresses[i] = new InternetAddress(recipients.get(i));
            }
            message.setRecipients(type, addresses);
        }
    }
    
    /**
     * Set message content (HTML, text, and attachments)
     * @param message MimeMessage
     * @param request MailRequest
     * @throws MessagingException if setting content fails
     */
    private void setMessageContent(MimeMessage message, MailRequest request) throws MessagingException, IOException {
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            // Use multipart for attachments
            MimeMultipart multipart = new MimeMultipart();
            
            // Add text/HTML body
            addBodyPart(multipart, request);
            
            // Add attachments
            addAttachments(multipart, request.getAttachments());
            
            message.setContent(multipart);
        } else {
            // Simple content without attachments
            if (request.getHtmlContent() != null && !request.getHtmlContent().trim().isEmpty()) {
                message.setContent(request.getHtmlContent(), "text/html; charset=UTF-8");
            } else if (request.getTextContent() != null && !request.getTextContent().trim().isEmpty()) {
                message.setContent(request.getTextContent(), "text/plain; charset=UTF-8");
            }
        }
    }
    
    /**
     * Add body part to multipart
     * @param multipart MimeMultipart
     * @param request MailRequest
     * @throws MessagingException if adding body part fails
     */
    private void addBodyPart(MimeMultipart multipart, MailRequest request) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        
        if (request.getHtmlContent() != null && !request.getHtmlContent().trim().isEmpty()) {
            bodyPart.setContent(request.getHtmlContent(), "text/html; charset=UTF-8");
        } else if (request.getTextContent() != null && !request.getTextContent().trim().isEmpty()) {
            bodyPart.setContent(request.getTextContent(), "text/plain; charset=UTF-8");
        }
        
        multipart.addBodyPart(bodyPart);
    }
    
    /**
     * Add attachments to multipart
     * @param multipart MimeMultipart
     * @param attachments List of attachments
     * @throws MessagingException if adding attachments fails
     */
    private void addAttachments(MimeMultipart multipart, List<MailRequest.Attachment> attachments)
            throws MessagingException, IOException {
        for (MailRequest.Attachment attachment : attachments) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            
            if (attachment.isFileBased()) {
                // File-based attachment
                File file = new File(attachment.getFilePath());
                if (!file.exists()) {
                    TestBase.logWarning("Attachment file not found: " + attachment.getFilePath());
                    continue;
                }
                attachmentPart.attachFile(file);
            } else if (attachment.isContentBased()) {
                // Content-based attachment
                attachmentPart.setContent(attachment.getContent(), 
                    attachment.getMimeType() != null ? attachment.getMimeType() : "application/octet-stream");
            }
            
            if (attachment.getFileName() != null && !attachment.getFileName().trim().isEmpty()) {
                attachmentPart.setFileName(attachment.getFileName());
            }
            
            multipart.addBodyPart(attachmentPart);
        }
    }
    
    /**
     * Create MailConfig from TestConfig (backward compatibility)
     * @return MailConfig
     */
    private static MailConfig createConfigFromTestConfig() {
        MailConfig config = new MailConfig();
        config.setSmtpHost(TestConfig.server);
        config.setUsername(TestConfig.from);
        config.setPassword(TestConfig.password);
        config.setSmtpPort(587); // Use STARTTLS by default
        config.setUseStartTLS(true);
        config.setUseSSL(false);
        config.setDebug(false);
        return config;
    }
    
    /**
     * SMTP Authenticator
     */
    private static class SMTPAuthenticator extends Authenticator {
        private final String username;
        private final String password;
        
        public SMTPAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
    
    /**
     * Test email sending functionality
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Example usage
            MailConfig config = MailConfig.createGmailConfig(TestConfig.from, TestConfig.password);
            MonitoringMail mailService = new MonitoringMail(config);
            
            MailRequest request = new MailRequest();
            request.setFrom(TestConfig.from);
            request.setTo(List.of(TestConfig.to));
            request.setSubject("Test Email");
//            request.setHtmlContent("<h1>Test Email</h1><p>This is a test email.</p>");
            String messageBody = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/job/DataDriven/HTML_20Report/";
            request.setTextContent(messageBody);
            
            mailService.sendMail(request);
            System.out.println("Email sent successfully!");
            
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}