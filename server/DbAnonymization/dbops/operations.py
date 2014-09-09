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
    Does

    :rtype : None
    :param db: Database object
    :param select_query: Will be executed and every row returned will be set
                         to user_id 0
    :return: Void

    :type db: Database
    :type select_query: String
    """
    cur = db.cursor
    print(select_query)
    cur.execute(select_query)
    rows = cur.fetchall()
    rowsToRename = []
    for row in rows:
        rowsToRename.append({"id": row[0]})
    cur.executemany("UPDATE trip SET user_id='0' WHERE trip_id=%(id)s", rowsToRename)
    print("Renamed "+str(len(rows))+" Elements")