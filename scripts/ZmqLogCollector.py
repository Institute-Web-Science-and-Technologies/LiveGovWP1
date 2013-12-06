import zmq
import threading
import time

SUBSCRIPTIONS = [
    "tcp://LG:50110",
    "tcp://LG:50111",
    "tcp://LG:50112",
    "tcp://LG:50113"
]


def main():
    Q = []

    inSock = initSubSocket(SUBSCRIPTIONS)

    appender = threading.Thread(target = doAppend, args=(inSock, Q))
    appender.daemon = True 
    appender.start()

    while (True):
        time.sleep(1)
        
        if (len(Q) > 5):
            print Q
            Q = []


def doAppend(sock, q):
    while (True):
        msg = sock.recv_string()

        print "recv ", msg

        q.append(msg)
    


def initSubSocket(SUBSCRIPTIONS):
    inSock = zmq.Context().socket(zmq.SUB)

    for addr in SUBSCRIPTIONS:
        inSock.connect(addr)
    
    inSock.setsockopt(zmq.SUBSCRIBE, "")

    return inSock


if __name__ == "__main__": main()
