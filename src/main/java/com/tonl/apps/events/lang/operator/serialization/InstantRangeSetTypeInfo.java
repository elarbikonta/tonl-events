package com.tonl.apps.events.lang.operator.serialization;

import com.google.common.collect.RangeSet;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Map;

//see: https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/dev/datastream/fault-tolerance/serialization/types_serialization/#defining-type-information-using-a-factory
public class InstantRangeSetTypeInfo extends TypeInfoFactory<RangeSet<Instant>> {

    @Override
    public TypeInformation<RangeSet<Instant>> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {

        TypeInformation<RangeSet<Instant>> info = TypeInformation.of(new TypeHint<RangeSet<Instant>>() {});

        return info;
    }


}