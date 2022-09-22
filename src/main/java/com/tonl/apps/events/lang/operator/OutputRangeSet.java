package com.tonl.apps.events.lang.operator;

import com.google.common.collect.RangeSet;
import com.tonl.apps.events.lang.operator.serialization.InstantRangeSetTypeInfo;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.flink.api.common.typeinfo.TypeInfo;

import java.io.Serializable;
import java.time.Instant;

@Data
@Accessors(chain = true)
public class OutputRangeSet implements Serializable {

    private String key;

    @TypeInfo(InstantRangeSetTypeInfo.class)
    private RangeSet<Instant> rangeSet;


}
