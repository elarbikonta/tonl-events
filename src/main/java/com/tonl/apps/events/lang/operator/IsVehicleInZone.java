package com.tonl.apps.events.lang.operator;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.tonl.apps.events.domain.geo.GeofenceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.time.Instant;

/**
 * isVehicleInZone(vehicleUid, geofenceUid): retourne true si le véhicule se trouve dans la zone spécifiée.
 * see: https://truckonline.atlassian.net/wiki/spaces/TDEV/pages/1825505291/Langage+et+op+rateurs#Geofencing
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class IsVehicleInZone extends RichMapFunction<GeofenceEvent, OutputRangeSet> {


    @Override
    public OutputRangeSet map(GeofenceEvent geofenceEvent) throws Exception {

        val output = new OutputRangeSet()
                .setKey(geofenceEvent.getKey())
                .setRangeSet(
                        ImmutableRangeSet.<Instant>builder()
                                .add(Range.atLeast(geofenceEvent.getGpsDate()))
                                .build()
                );


        System.out.println("IsVehicleInZone.map.output: " + output);

        return output;// rangetSet.ranges = [2022-09-22T01:00:00Z..+∞)

    }


}
