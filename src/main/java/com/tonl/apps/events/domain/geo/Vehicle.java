package com.tonl.apps.events.domain.geo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uid;
    private String vrn;
    private String customId;

}

