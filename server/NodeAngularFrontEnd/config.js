 // public | sensor_accelerometer       | table | liveandgov
 // public | sensor_gact                | table | liveandgov
 // public | sensor_gps                 | table | liveandgov
 // public | sensor_gravity             | table | liveandgov
 // public | sensor_gyroscope           | table | liveandgov
 // public | sensor_har                 | table | liveandgov
 // public | sensor_linear_acceleration | table | liveandgov
 // public | sensor_magnetic_field      | table | liveandgov
 // public | sensor_proximity           | table | liveandgov
 // public | sensor_rotation            | table | liveandgov
 // public | sensor_tags                | table | liveandgov
 // public | sensor_waiting             | table | liveandgov
 // public | trip                       | table | liveandgov


// sensor_gact
//  trip_id | ts | tag | confidence
// ---------+----+-----+------------
// (0 rows)

// sensor_gps
//  trip_id |      ts       |                       lonlat                       | altitude
// ---------+---------------+----------------------------------------------------+----------
//        1 | 1404914218200 | 0101000020E610000026EB26E77A591E404B1D893CEE2B4940 |

// sensor_har
//  trip_id |      ts       |   tag
// ---------+---------------+---------
//        8 | 1405006585838 | walking

// sensor_proximity
//  trip_id |      ts       |   key    | inside | of
// ---------+---------------+----------+--------+----
//        6 | 1405006548137 | platform | f      |

// sensor_tags
//  trip_id | ts | tag
// ---------+----+-----

// sensor_waiting
//  trip_id | ts | key | duration | at
// ---------+----+-----+----------+----





// SENSORS WITH X-, Y- AND Z-VALUES



var sensors = {
  motion: [
    // trip_id | ts | x | y | z
    'sensor_accelerometer',
    'sensor_gravity',
    'sensor_gyroscope',
    'sensor_linear_acceleration',
    'sensor_magnetic_field',
    'sensor_rotation'
  ]
};

var sensorDescription = {
  motion: {
    trip_id: Number,
    ts: Number,
    x: Number,
    y: Number,
    z: Number
  }
};


sensor_gact
sensor_gps
sensor_har
sensor_proximity
sensor_tags
sensor_waiting
