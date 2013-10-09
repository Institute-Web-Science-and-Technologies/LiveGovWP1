General
=======

All queries are exclusively HTTP-GET because we don't have anything to save yet.
The url contains the API version "/api/{VERSION}/".

Devices
=======

Currently we return all IDs, which have an entry inside the GPS table.
To get every ID simply fire a request against
```
  /api/1/devices
```
This returns an array of all device ids and also the amount of GPS samples gathered with this id.

GPS
===

To get all GPS samples for a given id query
```
  /api/1/:id/gps
```
This query accepts a limit parameter and also 
