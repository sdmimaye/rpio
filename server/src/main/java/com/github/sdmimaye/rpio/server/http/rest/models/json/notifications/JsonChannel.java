package com.github.sdmimaye.rpio.server.http.rest.models.json.notifications;

import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;

import java.util.ArrayList;
import java.util.List;

public class JsonChannel {
    private Long id;
    private String description;
    private boolean available;

    public static JsonChannel convert(NotificationChannel channel) {
        JsonChannel result = new JsonChannel();
        result.setId(channel.getId());
        result.setDescription(channel.getDescription());

        return result;
    }

    public static List<JsonChannel> convert(List<NotificationChannel> channels) {
        List<JsonChannel> result = new ArrayList<>();
        for (NotificationChannel channel : channels) {
            result.add(JsonChannel.convert(channel));
        }

        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
