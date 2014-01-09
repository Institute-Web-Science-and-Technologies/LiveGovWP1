# DATABASE

## Schema (deprecated)

    CREATE TABLE IF NOT EXISTS trip (trip_id SERIAL PRIMARY KEY, user_id VARCHAR(36), start_ts BIGINT, stop_ts BIGINT, name VARCHAR(255));

    CREATE TABLE IF NOT EXISTS sensor_accelerometer (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);
    CREATE TABLE IF NOT EXISTS sensor_linear_acceleration (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);
    CREATE TABLE IF NOT EXISTS sensor_gravity (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);

    CREATE TABLE IF NOT EXISTS sensor_gps (trip_id INT, ts BIGINT, lonlat GEOGRAPHY(Point));

    CREATE TABLE IF NOT EXISTS sensor_tags (trip_id INT, ts BIGINT, tag TEXT);

    CREATE TABLE IF NOT EXISTS har_annotation (trip_id INT, ts BIGINT, tag TEXT);
    CREATE TABLE IF NOT EXISTS sensor_google_activity (trip_id INT, ts BIGINT, activity TEXT);

## Schema (new)

    CREATE TABLE IF NOT EXISTS trip (id SERIAL PRIMARY KEY, user_id VARCHAR(36), start_ts BIGINT, stop_ts BIGINT, description VARCHAR(255));

    CREATE TABLE IF NOT EXISTS accelerometer (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);
    CREATE TABLE IF NOT EXISTS linear_acceleration (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);
    CREATE TABLE IF NOT EXISTS gravity (trip_id INT, ts BIGINT, x FLOAT, y FLOAT, z FLOAT);

    CREATE TABLE IF NOT EXISTS gps (trip_id INT, ts BIGINT, lonlat GEOGRAPHY(Point));

    CREATE TABLE IF NOT EXISTS tags (trip_id INT, ts BIGINT, tag TEXT);

    CREATE TABLE IF NOT EXISTS activity (trip_id INT, ts BIGINT, tag TEXT);
    CREATE TABLE IF NOT EXISTS activity_google (trip_id INT, ts BIGINT, activity TEXT);

## Dependencies

- postgis (available via homebrew)

## Dump

Do this on the remote host. Be careful!

### Dump globals

    pg_dumpall -g -U postgres > globals.sql

### Dump schema

    pg_dump -Fp -s -v -f schema.sql -U postgres liveandgov

### Dump row data from tables

    [...]

### Dump all table data (not recommended, too big)

    pg_dump -Fc -v -f full.dump -U postgres liveandgov

## Prepare local database

### Drop old database (if it already exists)

    dropdb liveandgov

### Create new database

    createdb -e -O postgres liveandgov

## Restore dumps

### tl;dr Handy one-liner:

		dropdb liveandgov && createdb -e -O postgres liveandgov && psql -Upostgres liveandgov -f schema/schema.sql && for i in tables/*; do psql -Upostgres -c "COPY ${i:t:r} FROM '$PWD/$i';" liveandgov; done

### Restore globals

    psql -f globals.sql

### Restore schema

    psql -f schema.sql liveandgov

### Copy data back to corresponding tables

    for i in *; do psql -Upostgres -c "COPY ${i:r} FROM '$PWD/$i';" liveandgov; done
