import zmq
import threading
import time
import requests
import json
from requests.auth import HTTPBasicAuth

SUBSCRIPTIONS = [
    "tcp://LG:50200",
    "tcp://LG:50201",
    "tcp://LG:50202",
    "tcp://LG:50203"
]

MAX_LEN = 2;
MAX_DELAY_SEC = 10;

UPLOAD_URL = "http://localhost:50220"

def main():
    Q = []

    inSock = initSubSocket(SUBSCRIPTIONS)

    last_time = time.time()

    while (True):
        msg = inSock.recv_string()

        print int(time.time() - last_time), "recv", msg

        Q.append(msg)
        
        if (len(Q) > MAX_LEN or time.time() - last_time > MAX_DELAY_SEC):
            print "Sending out collected Logs."
            # send(Q)
            Q = []
            last_time = time.time()

def send(Q):
    log_str = "\n".join(Q)
    meta =  {"filename":"logfile.txt", "customerid":1, "registereduserid":3 }
    files = {'file': ('log.txt', log_str),
             'meta': ('meta.txt', json.dumps(meta)) }
    r = requests.post(UPLOAD_URL, files=files, auth=HTTPBasicAuth('user', 'pass'))
    print r.text


def initSubSocket(SUBSCRIPTIONS):
    inSock = zmq.Context().socket(zmq.SUB)

    for addr in SUBSCRIPTIONS:
        print "Connecting to: ", addr
        inSock.connect(addr)
    
    inSock.setsockopt(zmq.SUBSCRIBE, "")

    return inSock


if __name__ == "__main__": main()
