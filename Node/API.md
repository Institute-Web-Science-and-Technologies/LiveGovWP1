General
=======

All queries are exclusively HTTP-GET because we don't have anything to save yet.
The url contains the API version "/api/{VERSION}/".

Devices
=======

Currently we return all IDs, which have an entry inside the GPS table.
To get every ID simply fire a request against
```
  ;http://141.26.71.84:3000/api/1/devices
  
  $ curl http://141.26.71.84:3000/api/1/devices
  [{"devId":"hel-26-notag-150","gpsCount":"150"},...]
```
This returns an array of all device ids and also the amount of GPS samples gathered with this id.

GPS
===

:id/gps
-------

Returns every gps point recorded for the given device.

```
  ;http://141.26.71.84:3000/api/1/:id/gps
  
  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/gps
  [{"ts":"2013-10-09T02:38:11.632Z","lon":50.362944,"lat":7.5592947}, ...]
```

:id/gps/count
-------------

Returns the number of gps points stored

```
  ;http://141.26.71.84:3000/api/1/:id/gps/count
  
  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/gps/count
  {"count":"71"}
```

Tags
====

:id/tag
-------

Returns all tags recorded by the given device

```
  ;http://141.26.71.84:3000/api/1/:id/tag
  
  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/tag
  [{"ts":"2013-10-09T11:37:21.513Z","tag":"waljing"},...]
```

Accelerometer/Linear Acceleration/Gravity
=========================================

All API calls are the same here. Just replace "acc" with "lac" (for linear acceleration)  or "gra" (for gravity).

:id/acc
-------

Returns a subsampled array of accelerometer values.

### Query Paramater

#### window (optional)
Default: 200
Type: int

Change the number of windows, which are eavenly filled with values. If there are less values than the number of windows requested it will just return all samples.

#### startTime / endTime (optional)
Type: int

These values define the start and end time. Each can stand by itself. The value will be parsed as an integer and will be used as a timestamp.

```javascript
var start = new Date(parseInt(request.query.startTime));
```

```
  ;http://141.26.71.84:3000/api/1/:id/acc
  
  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/acc
  [{"window":1,
    "avgX":-1.62283134460449,
    "minX":-3.82003784179688,
    "maxX":0.1658935546875,
    "avgY":3.0321896870931,
    "minY":1.66316223144531,
    "maxY":4.32203674316406,
    "avgZ":9.22814687093099,
    "minZ":7.75830078125,
    "maxZ":10.595703125,
    "startTime":1381290079039,
    "endTime":1381290081919,
    "midTime":1381290080479}, ... ]
    
  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/acc?windows=1&startTime=1381309499871
  [{"window":1,
    "avgX":-0.188089454600529,
    "minX":-32.0238647460938,
    "maxX":37.723388671875,
    "avgY":-0.258492997471928,
    "minY":-38.9804840087891,
    "maxY":14.8420867919922,
    "avgZ":8.49373825831147,
    "minZ":-38.8981628417969,
    "maxZ":26.1430969238281,
    "startTime":1381290079039,
    "endTime":1381328920704,
    "midTime":1381309499871}]
```

:id/acc/count
-------------

Returns the number of accelerometer samples collected by the given device.

```
  ;http://141.26.71.84:3000/api/1/:id/acc/count

  $ curl http://141.26.71.84:3000/api/1/61c206d1a77d509e/acc/count
  {"count":"28790"}
```

