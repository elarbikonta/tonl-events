package com.tonl.apps.events.domain.geo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class GeofenceEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Instant gpsDate;
    private Driver driver;
    private String event;


    public String getDriverUid() {
        return getDriver().getUid();
    }

    public String getKey() {
        return getDriverUid();
    }


}

