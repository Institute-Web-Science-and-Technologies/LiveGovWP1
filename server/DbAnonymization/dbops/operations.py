from database import Database
import time

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
        print(cur.query)
    except:                                         # TODO: Add Exception Type!
        print "Error while cleanup DB for do_cleanup"
    print("Cleanup done")


def do_anonymization(db, select_query):
    """


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
    cur.executemany("UPDATE trip SET user_id='0' WHERE trip_id=%(id)s", rowsToRename)
    print("Renamed "+str(len(rows))+" Elements")

def do_user_cut(db, radius, k):
    """
    Removes all lines, which does not have

    :param db: Database object
    :param radius: The radius in meters
    :param k: number of other users that must have samples inside the area
    :return:
    """
    # Query to get all points which have an gps point of k distinct users
    # and insert it into the database:
    query = """
    INSERT INTO anon_gps (lonlat, trip_id, ts, altitude) (
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