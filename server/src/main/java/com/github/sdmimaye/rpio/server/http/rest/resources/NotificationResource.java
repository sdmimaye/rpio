package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationChannelDao;
import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationDao;
import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationSubscriptionDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.models.json.notifications.JsonChannel;
import com.github.sdmimaye.rpio.server.http.rest.models.json.notifications.JsonNotification;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.services.notifications.NotificationService;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessageParser;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notifications")
public class NotificationResource {
    private final HibernateUtil hibernateUtil;
    private final UserSessionUtil userSessionUtil;
    private final NotificationMessageParser parser;
    private final NotificationDao notificationDao;
    private final NotificationService service;
    private final NotificationSubscriptionDao notificationSubscriptionDao;
    private final NotificationChannelDao notificationChannelDao;

    @Inject
    public NotificationResource(HibernateUtil hibernateUtil, UserSessionUtil userSessionUtil, NotificationMessageParser parser,
                                NotificationDao notificationDao, NotificationService service, NotificationSubscriptionDao notificationSubscriptionDao,
                                NotificationChannelDao notificationChannelDao) {
        this.hibernateUtil = hibernateUtil;
        this.userSessionUtil = userSessionUtil;
        this.parser = parser;
        this.notificationDao = notificationDao;
        this.service = service;
        this.notificationSubscriptionDao = notificationSubscriptionDao;
        this.notificationChannelDao = notificationChannelDao;
    }

    @GET
    @RolesAllowed("notifications-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonNotification> handleGetAll(@Context HttpServletRequest request) {
        User user = userSessionUtil.loadUser(request.getSession());
        if(user == null)
            throw new WebApplicationException(HttpStatus.SC_FORBIDDEN);

        List<Notification> all = notificationDao.getAllOrdered();
        return JsonNotification.convert(user, all);
    }

    @GET
    @Path("/channels")
    @RolesAllowed("notifications-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonChannel> handleGetAllChannels(@Context HttpServletRequest request) {
        User user = userSessionUtil.loadUser(request.getSession());
        if(user == null)
            throw new WebApplicationException(HttpStatus.SC_FORBIDDEN);

        List<JsonChannel> channels = JsonChannel.convert(notificationChannelDao.getAll());
        for (JsonChannel channel : channels) {
            channel.setAvailable(service.isChannelAvailable(user, channel.getDescription()));
        }

        return channels;
    }

    @POST
    @RolesAllowed("notifications-read")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handlePost(@Context HttpServletRequest request, List<JsonNotification> notifications) {
        User user = userSessionUtil.loadUser(request.getSession());
        if(user == null)
            throw new WebApplicationException(HttpStatus.SC_FORBIDDEN);

        for (JsonNotification notification : notifications) {
            Notification existing = notificationDao.getById(notification.getId());
            if(existing == null)
                throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

            if (notification.getChannel().equals(JsonNotification.UNSUED_CHANNEL_IDENTIFIER)) {
                user.unsubscribe(existing, notificationSubscriptionDao);
            }else {
                NotificationChannel channel = notificationChannelDao.getByDescription(notification.getChannel());
                if(channel == null)
                    throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

                if(!service.isChannelAvailable(user, channel.getDescription()))
                    throw new WebApplicationException(HttpStatus.SC_BAD_REQUEST);

                user.subscribe(existing, channel, notificationSubscriptionDao);
            }
        }
        hibernateUtil.commitAndClose();
        return Response.accepted().build();
    }

    @GET
    @Path("/templates/{id}")
    @RolesAllowed("notifications-read")
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationMessage getNotificationTemplate(@PathParam("id") String notificationId) {
        Notification notification = notificationDao.getById(notificationId);
        if(notification == null)
            throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

        NotificationMessage message = parser.raw(notification);
        if(message == null)
            throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

        return message;
    }

    @GET
    @Path("/templates/unmodified/{id}")
    @RolesAllowed("notifications-read")
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationMessage getUnmodifiedNotificationTemplate(@PathParam("id") String notificationId) {
        Notification notification = notificationDao.getById(notificationId);
        if(notification == null)
            throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

        NotificationMessage message = parser.getUnmodifiedTemplate(notification);
        if(message == null)
            throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

        return message;
    }

    @POST
    @Path("/templates/{id}")
    @RolesAllowed("notifications-read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveNotificationTemplate(@PathParam("id") String notificationId, NotificationMessage message) {
        if(StringUtils.isBlank(message.getMessage()) || StringUtils.isBlank(message.getSubject()))
            throw new WebApplicationException(HttpStatus.SC_BAD_REQUEST);

        Notification notification = notificationDao.getById(notificationId);
        if(notification == null)
            throw new WebApplicationException(HttpStatus.SC_NOT_FOUND);

        parser.save(notification, message);
        hibernateUtil.commitAndClose();

        return Response.accepted().build();
    }
}
