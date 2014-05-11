package eu.liveandgov.wp1.tests;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.DB;
import eu.liveandgov.wp1.pipeline.impl.ItemDB;
import eu.liveandgov.wp1.pipeline.impl.RowDB;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Härtel on 10.05.2014.
 */
public class Pivoter {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        /**
         * Requires representational view on sensor_gps:
         * CREATE OR REPLACE VIEW representation_gps AS SELECT TEXT ('GPS') AS type, ts, user_id, ST_X(lonlat::geometry) AS lat, ST_Y(lonlat::geometry) AS lon FROM sensor_gps INNER JOIN trip ON sensor_gps.trip_id=trip.trip_id;
         */
//        ItemDB itemDB = new ItemDB(scheduledExecutorService, "jdbc:postgresql:data", "user", "default", "representation_gps");
//
//        itemDB.setConsumer(new Consumer<Item>() {
//            @Override
//            public void push(Item item) {
//                System.out.println(item.toString());
//            }
//        });
//
//        itemDB.load();


        RowDB trip = new RowDB(scheduledExecutorService, "jdbc:postgresql:data", "user", "default", "trip");
        //  trip.manual.add("trip_id");
        //  trip.autoInsertCount = 1;

        trip.setConsumer(new Consumer<Map<String, Object>>() {
            @Override
            public void push(Map<String, Object> stringObjectMap) {
                System.out.println(stringObjectMap.toString());
            }
        });

        trip.keysGenerated.register(new Callback<Map<String, Object>>() {
            @Override
            public void call(Map<String, Object> stringObjectMap) {
                System.out.println("Keys generated: " + stringObjectMap);
            }
        });

        Map<String, Object> oneTrip = new TreeMap<String, Object>();
        oneTrip.put("start_ts", 6);
        oneTrip.put("stop_ts", 7);
        oneTrip.put("user_id", "Kekeke");
        oneTrip.put("name", "Schli schla");
        trip.push(oneTrip);
        trip.insertNow();

        scheduledExecutorService.shutdown();
        scheduledExecutorService.awaitTermination(3L, TimeUnit.SECONDS);
    }

}
