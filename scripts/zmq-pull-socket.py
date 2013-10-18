import zmq
import time

PORT = "5555";

c = zmq.Context()
s = c.socket(zmq.PULL)
s.bind("tcp://*:" + PORT)

print "Listening for ZMQ connections on port " + PORT

while True:
   print s.recv()
