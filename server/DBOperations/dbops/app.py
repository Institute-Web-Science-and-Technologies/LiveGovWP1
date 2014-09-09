__author__ = 'Christoph Ehlen'

import sys
import getopt
from database import Database
from operations import do_anonymization, do_cleanup

validOps = ["cleanup", "anonymization"]
callOps = {
    "cleanup": lambda db, args: do_cleanup(db),
    "anonymization": lambda db, args: do_anonymization(db, args[0])
}


def print_help():
    print """
Usage: app.py [-d database name] [-u database user] [-p database password]
              [-h database host] [-o operation]

Valid Operations:
    cleanup: Flags all expired rows as deleted
             No Options

    anonymization: Sets all user_ids to 0
        Option:
            SQL Select Query: Every row found by this query will be set
            to user_id = 0.
    """


def main(argv):
    if len(argv) == 0:
        print_help()
        return
    options = getopt.getopt(argv, "d:u:p:h:o:")
    db = "liveandgov_dev"
    user = "liveandgov"
    password = ""
    host = "localhost"
    operation = ""
    for (op, v) in options[0]:
        if op == "-d":
            db = v
        elif op == "-u":
            user = v
        elif op == "-p":
            password = v
        elif op == "-h":
            host = v
        elif op == "-o":
            operation = v
    if operation not in validOps:
        print("ERROR: Operation is not valid!")
        print_help()
        return
    print("Running operation " + operation + "...")
    database = Database(host, db, user, password)
    print(options)
    callOps[operation](database, options[1])

if __name__ == "__main__":
    main(sys.argv[1:])