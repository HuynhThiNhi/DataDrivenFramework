package com.w2a.utilities;

import java.util.Properties;

/**
 * Configuration class for email settings
 * Centralizes all SMTP configuration properties
 */
public class MailConfig {
    private String smtpHost;
    private int smtpPort = 587;
    private String username;
    private String password;
    private boolean useSSL = false;
    private boolean useStartTLS = true;
    private int connectionTimeout = 10000;
    private int timeout = 10000;
    private int writeTimeout = 10000;
    private boolean debug = false;

    // Constructors
    public MailConfig() {}

    public MailConfig(String smtpHost, String username, String password) {
        this.smtpHost = smtpHost;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

    public int getSmtpPort() { return smtpPort; }
    public void setSmtpPort(int smtpPort) { this.smtpPort = smtpPort; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isUseSSL() { return useSSL; }
    public void setUseSSL(boolean useSSL) { this.useSSL = useSSL; }

    public boolean isUseStartTLS() { return useStartTLS; }
    public void setUseStartTLS(boolean useStartTLS) { this.useStartTLS = useStartTLS; }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public int getWriteTimeout() { return writeTimeout; }
    public void setWriteTimeout(int writeTimeout) { this.writeTimeout = writeTimeout; }

    public boolean isDebug() { return debug; }
    public void setDebug(boolean debug) { this.debug = debug; }

    /**
     * Creates Properties object for JavaMail Session
     * @return Properties configured for SMTP
     */
    public Properties toProperties() {
        Properties props = new Properties();
        
        // Basic SMTP settings
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.auth", "true");
        
        // Timeout settings
        props.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
        props.put("mail.smtp.timeout", String.valueOf(timeout));
        props.put("mail.smtp.writetimeout", String.valueOf(writeTimeout));
        
        // Debug setting
        props.put("mail.debug", String.valueOf(debug));
        
        // SSL/TLS configuration
        if (useSSL) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", smtpHost);
            // For SSL on port 465
            if (smtpPort == 465) {
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
                props.put("mail.smtp.socketFactory.port", "465");
            }
        } else if (useStartTLS) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        
        return props;
    }

    /**
     * Validates the configuration
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return smtpHost != null && !smtpHost.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               smtpPort > 0 && smtpPort <= 65535;
    }

    /**
     * Gets validation errors
     * @return String containing validation errors
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (smtpHost == null || smtpHost.trim().isEmpty()) {
            errors.append("SMTP host is required. ");
        }
        if (username == null || username.trim().isEmpty()) {
            errors.append("Username is required. ");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.append("Password is required. ");
        }
        if (smtpPort <= 0 || smtpPort > 65535) {
            errors.append("SMTP port must be between 1 and 65535. ");
        }
        if (useSSL && useStartTLS) {
            errors.append("Cannot use both SSL and STARTTLS. Choose one. ");
        }
        return errors.toString().trim();
    }

    /**
     * Creates a default Gmail configuration
     * @param username Gmail username
     * @param password Gmail app password
     * @return MailConfig for Gmail
     */
    public static MailConfig createGmailConfig(String username, String password) {
        MailConfig config = new MailConfig("smtp.gmail.com", username, password);
        config.setSmtpPort(587);
        config.setUseStartTLS(true);
        config.setUseSSL(false);
        return config;
    }

    /**
     * Creates a default Outlook configuration
     * @param username Outlook username
     * @param password Outlook password
     * @return MailConfig for Outlook
     */
    public static MailConfig createOutlookConfig(String username, String password) {
        MailConfig config = new MailConfig("smtp-mail.outlook.com", username, password);
        config.setSmtpPort(587);
        config.setUseStartTLS(true);
        config.setUseSSL(false);
        return config;
    }
}
