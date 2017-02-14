package com.github.sdmimaye.rpio.server.net.smtp;

import com.github.sdmimaye.rpio.server.database.models.enums.SmtpSecurityType;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class FluentEmail {
    private static final Logger logger = LoggerFactory.getLogger(FluentEmail.class);
    private Email email;

    private FluentEmail(Email email) {
        this.email = email;
    }

    public static FluentEmail plain() {
        logger.info("Generating new plain-email...");
        return new FluentEmail(new SimpleEmail());
    }

    public static FluentEmail html(){
        logger.info("Generating new html-email...");
        return new FluentEmail(new HtmlEmail());
    }

    public static FluentEmail multipart() {
        logger.info("Generating new multi-part-email...");
        return new FluentEmail(new MultiPartEmail());
    }

    public FluentEmail attachment(URL url, String filename) {
        MultiPartEmail mpe = asMultipart();
        EmailAttachment attachment = new EmailAttachment();
        attachment.setURL(url);
        attachment.setName(filename);
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        try {
            mpe.attach(attachment);
        } catch (EmailException e) {
            logger.error("Error while appending attachment", e);
            throw new RuntimeException(e);
        }

        return this;
    }

    public FluentEmail host(String host) {
        email.setHostName(host);
        return this;
    }

    public FluentEmail port(int port) {
        email.setSmtpPort(port);
        email.setSslSmtpPort(String.valueOf(port));

        return this;
    }

    public FluentEmail timeout(int timeoutInMilliseconds) {
        email.setSocketTimeout(timeoutInMilliseconds);
        email.setSocketConnectionTimeout(timeoutInMilliseconds);

        return this;
    }

    public FluentEmail to(String emailaddress, String name) {
        try {
            email.addTo(emailaddress, name);
        } catch (EmailException e) {
            logger.error("Could not add receivere to email", e);
            throw new RuntimeException(e);
        }
        return this;
    }

    public FluentEmail from(String emailaddress, String name) {
        try {
            email.setFrom(emailaddress, name);
        } catch (EmailException e) {
            logger.error("Could not set sender in email", e);
            throw new RuntimeException(e);
        }
        return this;
    }

    public FluentEmail subject(String subject) {
        email.setSubject(subject);
        return this;
    }

    public void send() throws EmailException {
        email.send();
    }

    public boolean sendFailsafe() {
        try {
            email.send();
            return true;
        } catch (EmailException e) {
            logger.error("Error while sending email: " + e.getMessage() + ", Reason: " + e.getCause().getMessage());
            return false;
        }
    }

    public FluentEmail secure(boolean secure) {
        email.setStartTLSEnabled(secure);
        email.setStartTLSRequired(secure);
        email.setSSLOnConnect(secure);
        email.setSSLCheckServerIdentity(secure);

        return this;
    }

    public FluentEmail secure(SmtpSecurityType settings) {
        switch (settings) {
            case NONE:
                email.setStartTLSEnabled(false);
                email.setStartTLSRequired(false);
                email.setSSLOnConnect(false);
                break;
            case SSLTLS:
                email.setStartTLSEnabled(true);
                email.setStartTLSRequired(true);
                email.setSSLOnConnect(true);
                break;
            case StartTLS:
                email.setStartTLSEnabled(true);
                email.setStartTLSRequired(true);
                email.setSSLOnConnect(false);
                break;
        }

        return this;
    }

    public FluentEmail message(String message) {
        try {
            email.setMsg(message);
        } catch (EmailException e) {
            logger.error("Could not set message in email", e);
            throw new RuntimeException(e);
        }

        return this;
    }

    public FluentEmail htmlMessage(String message) {
        HtmlEmail htmlEmail = asHtml();
        try {
            htmlEmail.setHtmlMsg(message);
        } catch (EmailException e) {
            logger.error("Could not set html message", e);
            throw new RuntimeException(e);
        }

        return this;
    }

    public FluentEmail authentication(String user, String password) {
        email.setAuthentication(user, password);
        return this;
    }

    private MultiPartEmail asMultipart() {
        MultiPartEmail multiPartEmail = email instanceof MultiPartEmail ? (MultiPartEmail) email : null;
        if(multiPartEmail == null)
            throw new RuntimeException("Please use a multipart-email if you need to append attachments!");

        return multiPartEmail;
    }

    private HtmlEmail asHtml() {
        HtmlEmail htmlEmail = email instanceof HtmlEmail ? (HtmlEmail) email : null;
        if(htmlEmail == null)
            throw new RuntimeException("Please use a html-email if you need to set html content!");

        return htmlEmail;
    }
}
