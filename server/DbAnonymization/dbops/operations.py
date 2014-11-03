from database import Database
import time
import geopy
from geopy.distance import VincentyDistance
import re
import random

__author__ = 'Christoph Ehlen'


def do_cleanup(db):
    """
    Checks the whole database and sets every expired entry to deleted

    :param db: Database object
    :return: None

    :type db: Database
    """
    cur = db.cursor
    try:
        cur.execute("UPDATE trip SET deleted=true WHERE expires<=%(now)s",
                    {"now": int(time.time() * 1000)})
        db.connection.commit()
    except:  # TODO: Add Exception Type!
        print "Error while cleanup DB for do_cleanup"
    print("Cleanup done")


def do_anonymization(db, select_query):
    """
    Sets every user_id to 0

    :rtype : None
    :param db: Database object
    :param select_query: Will be executed and every row returned will be set
                         to user_id 0
    :return: Void

    :type db: Database
    :type select_query: String
    """
    cur = db.cursor
    cur.execute(select_query)
    rows = cur.fetchall()
    rowsToRename = []
    for row in rows:
        rowsToRename.append({"id": row[0]})
    cur.executemany("UPDATE trip SET user_id='0' WHERE trip_id=%(id)s",
                    rowsToRename)
    db.connection.commit()
    print("Renamed " + str(len(rows)) + " Elements")


def do_user_cut(db, radius, k):
    """
    Removes all lines, which does not have at least one GPS point of K users
    inside the radius

    :param db: Database object
    :param radius: The radius in meters
    :param k: number of other users that must have samples inside the area
    :return:
    """
    # Query to get all points which have an gps point of k distinct users
    # and insert it into the database:
    query = """
    INSERT INTO anon_sensor_gps (lonlat, trip_id, ts, altitude) (
        SELECT
            p.lonlat, p.trip_id, p.ts, p.altitude
        FROM (SELECT
            gps.lonlat, gps.trip_id, gps.ts, gps.altitude, trip.user_id
            FROM sensor_gps gps, trip
            WHERE trip.trip_id = gps.trip_id
        ) p,
        (SELECT
            gps.lonlat, gps.trip_id, gps.ts, gps.altitude, trip.user_id
            FROM sensor_gps gps, trip
            WHERE trip.trip_id = gps.trip_id
        ) test
        WHERE
            p.user_id != test.user_id
        AND
            ST_DWITHIN(test.lonlat, p.lonlat, """ + str(radius) + """)
        GROUP BY p.lonlat, p.trip_id, p.ts, p.user_id, p.altitude
        HAVING count(*) >= """ + str(k) + """
    );
    """
    cur = db.cursor
    cur.execute(query)
    db.connection.commit()


def do_blur(db, radius):
    """
    Offsets every GPS point by a random amount (0, radius[ in a random
    direction

    :param db: Database object
    :param radius: the maximum distance to offset
    :return: Void
    """
    select_query = """
        SELECT ST_AsText(lonlat) as point, trip_id, ts, altitude
        FROM sensor_gps LIMIT 1"""
    radius = int(radius)
    cur = db.cursor
    cur.execute(select_query)
    rows = cur.fetchall()
    pointre = re.compile("POINT\((\d+\.\d+) (\d+\.\d+)\)")
    insertdata = []
    for row in rows:
        match = pointre.match(row[0])
        lat = float(match.group(1))
        lon = float(match.group(2))
        origin = geopy.Point(lat, lon)
        bearing = random.randint(0, 360)
        distance = random.randint(0, radius) / 1000
        destination = VincentyDistance(kilometers=distance)\
                       .destination(origin, bearing)
        insertdata.append({"point": "POINT(" + str(destination.latitude) + " "
                                    + str(destination.longitude) + ")",
                           "trip_id": row[1], "ts": row[2],
                           "altitude": row[3]})
    print("Done Select, starting insert")
    cur.executemany("""
    INSERT INTO anon_sensor_gps (lonlat, trip_id, ts, altitude) VALUES (
    ST_GeomFromText(%(point)s), %(trip_id)s, %(ts)s, %(altitude)s);
    """, insertdata)
    print("Done")
    db.connection.commit()


def do_k_anonymity(db, k, max_range):
    print(k, max_range)
    """
    Creates a centroid for every GPS point from the k nearest neigbours
    :param db: Database object
    :param k: Number of points to grab
    :param max_range: Maximum range in which the points can be
    :return: Void
    """
    select_query = """SELECT ST_AsText(lonlat), trip_id, ts, altitude FROM
    sensor_gps"""
    cur = db.cursor
    cur.execute(select_query)
    rows = cur.fetchall()
    print("Processing "+str(len(rows))+" Points")
    for row in rows:
        # get the centroid
        point = "ST_GeomFromText('"+row[0]+"')"
        limit = ''
        if k != 0:
            limit = " LIMIT " + k
        select_k = """
            SELECT ST_AsText(ST_Centroid(ST_Collect(ARRAY(SELECT ST_AsText(lonlat) FROM sensor_gps
            WHERE ST_Distance(lonlat, """+point+""") <= """+max_range+"""
            ORDER BY ST_Distance(lonlat, """+point+""") ASC"""+limit+"))))"
        cur.execute(select_k)
        for cent in cur.fetchall():
            # insert the centroid
            altitude = "NULL"
            if row[3]:
                altitude = "'"+str(row[3])+"'"

            insert_query = """
            INSERT INTO anon_sensor_gps (lonlat, trip_id, ts, altitude)
            VALUES (ST_GeomFromText('"""+cent[0]+"""'), '"""+str(row[1])+"""',
            '"""+str(row[2])+"""', """+str(altitude)+""")"""
            cur.execute(insert_query)
    print("Done")
    db.connection   .commit()