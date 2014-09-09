import psycopg2
import psycopg2.extras

__author__ = 'Christoph Ehlen'


class Database:
    """
    Database adapter
    """
    def __init__(self, host, dbname, user, password):
        self._conn = None
        self._host = host
        self._dbname = dbname
        self._user = user
        self._password = password
        self._cur = None

    def connect(self):
        db_string = " ".join([
            "dbname='"+self._dbname+"'",
            "host='"+self._host+"'",
            "user='"+self._user+"'",
            "password='"+self._password+"'"
        ])
        try:
            self._conn = psycopg2.connect(db_string)
            self._cur = self._conn.cursor()
        except:                                  # TODO: Change to DB Exception
            print("Could not connect to the database!")

    @property
    def cursor(self):
        return self.connection.cursor()

    @property
    def connection(self):
        if not self._conn:  # TODO: Add is connected check, so we can reconnect
            self.connect()
        return self._conn