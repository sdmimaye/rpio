package com.github.sdmimaye.rpio.server.services.notifications.channels;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.SmtpConfig;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.net.smtp.FluentEmail;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class EmailChannel extends Channel {
    private final Configuration configuration;

    @Inject
    public EmailChannel(Configuration configuration) {
        this.configuration = configuration;
    }

    public static String getChannelDescription() {
        Class type = EmailChannel.class;
        return type.getSimpleName();
    }

    @Override
    public void send(User receiver, NotificationMessage message) {
        if (!isAvailable(receiver))
            throw new RuntimeException("Either the smtp settings are wrong or the user: " + receiver.getLoginName() + " has no valid email address");

        SmtpConfig config = configuration.read(SmtpConfig.class);
        FluentEmail email = FluentEmail.plain()
                                       .from(config.getEmail(), config.getName())
                                       .to(receiver.getEmail(), receiver.getLoginName())
                                       .secure(config.getSecurity())
                                       .host(config.getHost())
                                       .port(config.getPort())
                                       .timeout(5000)
                                       .authentication(config.getLogin(), config.getPassword())
                                       .subject(message.getSubject())
                                       .message(message.getMessage());

        if (!email.sendFailsafe())
            throw new RuntimeException("Could not send email");
    }

    @Override
    public boolean isAvailable(User receiver) {
        SmtpConfig config = configuration.read(SmtpConfig.class);
        if(config == null || !config.isValidConfiguration())
            return false;

        if (StringUtils.isBlank(receiver.getEmail()))
            return false;

        return true;
    }
}
