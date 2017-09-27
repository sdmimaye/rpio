package com.github.sdmimaye.rpio.server.database.migrations.implementations;

import com.github.sdmimaye.rpio.server.database.dao.gpio.GpioPinDao;
import com.github.sdmimaye.rpio.server.database.migrations.Migration;
import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PinLogicMigration implements Migration {
    private final GpioPinDao dao;

    @Inject
    public PinLogicMigration(GpioPinDao dao) {
        this.dao = dao;
    }

    @Override
    public String identifier() {
        return "PinLogicMigration from: " + LocalDateTime.of(2017, 5, 3, 19, 9).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public void performUpdate() {
        dao.getAll().forEach(gp ->{
            if(gp.getLogic() == null) gp.setLogic(PinLogic.NORMAL);
        });
    }
}
