package com.tonl.apps.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.RangeSet;
import com.tonl.apps.events.domain.geo.GeofenceEvent;
import com.tonl.apps.events.lang.operator.IsVehicleInZone;
import com.tonl.apps.events.lang.operator.OutputRangeSet;
import com.tonl.apps.events.util.ParallelTestSource;
import com.twitter.chill.protobuf.ProtobufSerializer;
import lombok.val;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.Test;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IsVehicleInZoneTest {

    private static final int PARALLELISM = 2;

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
                    .setNumberSlotsPerTaskManager(PARALLELISM).setNumberTaskManagers(1).build());
    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    public void operatorChronograph() throws Exception {

        // set up streaming execution environment
        val env = StreamExecutionEnvironment.getExecutionEnvironment();

        final ExecutionConfig config = env.getConfig();

        // register the Google Protobuf serializer with Kryo
        env.getConfig().registerTypeWithKryoSerializer(RangeSet.class, ProtobufSerializer.class);


        // create event data streams
        List<GeofenceEvent> geofenceEvents = getGeofenceEvents("streams/geofencing/geofence-events.json");
        val geofenceSource = sourceFromArray(geofenceEvents.toArray(new GeofenceEvent[]{}));
        KeyedStream<GeofenceEvent, String> geofenceDS = getGeofenceDS(env, geofenceSource, GeofenceEvent::getDriverUid);


        val dataStream = geofenceDS
                .map(new IsVehicleInZone())
                .keyBy(OutputRangeSet::getKey);


        val resultList = new ArrayList<OutputRangeSet>();
        dataStream.executeAndCollect().forEachRemaining(resultList::add);
        assertThat(resultList).hasSize(2);
        final RangeSet<Instant> rangeSet = resultList.get(0).getRangeSet(); // rangetSet.ranges = null !



        //TODO: find proper serializer (otherwise cannot print results as expected)
        System.out.println(resultList);

    }


    public List<GeofenceEvent> getGeofenceEvents(final String jsonFileName) throws java.io.IOException {
        return mapper.readValue(new URL("file:src/test/resources/" + jsonFileName),
                new TypeReference<List<GeofenceEvent>>() {
                });
    }

    private ParallelTestSource<GeofenceEvent> sourceFromArray(GeofenceEvent[] geofenceEvents) {
        return new ParallelTestSource<>(geofenceEvents);
    }

    private KeyedStream<GeofenceEvent, String> getGeofenceDS(StreamExecutionEnvironment localExecutionEnv,
                                                             ParallelTestSource<GeofenceEvent> source2,
                                                             KeySelector<GeofenceEvent, String> keySelector) {

        val watermarkStrategy2 = geofenceStreamWatermarkStrategy();

        return localExecutionEnv.addSource(source2)
                .assignTimestampsAndWatermarks(watermarkStrategy2)
                .keyBy(keySelector);

    }

    private WatermarkStrategy<GeofenceEvent> geofenceStreamWatermarkStrategy() {
        return WatermarkStrategy.
                <GeofenceEvent>forMonotonousTimestamps()
                // <ActivityEvent>forBoundedOutOfOrderness(Duration.ofSeconds(59))
                .withTimestampAssigner((activity, streamRecordTimestamp) -> activity.getGpsDate().toEpochMilli());
    }

}
