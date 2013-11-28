""" 

This script was developed because of performance issues with having all route coordinates in one big "snippets" table. 
The HSL gtfs.xml ETL (...yeah! TLAs FTW) process was developed incremental and is not straight forward. It was done in the following steps:
  
  1. Extract and import the XML data provided by HSL with the google framework described in "GtfsDataETL.txt".
  2. After step 1, you get a nice normalized database schema. Unfortunately this design doesn't support our kind of queries very well. Therefore we split all  trips contained in the "trips" table into small snippets and precompute the corresponding arrival time for each snippet (c.f. DivideAndConquerGtfs.java).
  3. The "snippets" table contains more than 300 million rows. The fact, that most points are clustered in the city center of Helsinki, tests the limits of the PostGIS index. Query response times of 30 seconds and more are not feasible in our use case. To lower the query time, we partition the "snippets" table horizontally by using the SQL generated with the script below. The data is split in 7 master tables. One table for each day. Each day table gets for each hour an additional subtable. In total we generate 7 master tables plus 7 * 24 subtables.    

The intermediate artifacts are xml -> normalized tables -> one big csv file -> one big "snippets" table -> 7 + 7*24 tables.
 
"""
  
for d in ["monday","tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]:
	masterTName = "snippets_"+d
	print """CREATE TABLE IF NOT EXISTS """+masterTName+""" (route_id VARCHAR(20), shape_id VARCHAR(100), trip_id VARCHAR(100), geom GEOMETRY(POINT,4326), arrival_time INTEGER);"""
	for i in range(0,25):
	  boundaryFrom = str(i * 3600)
	  boundaryTo = str(i * 3600 + 3600)
	  tname = masterTName + boundaryFrom + "_" + boundaryTo
	  print """
	CREATE TABLE IF NOT EXISTS """ + tname +""" (
	       CHECK ( arrival_time >= """ + boundaryFrom + """ AND arrival_time < """ + boundaryTo + """ )
	) INHERITS (""" + masterTName + """);

	INSERT INTO """ + tname + """
	SELECT trips_route_id,
	       shapes_shape_id,
	       trips_trip_id,
	       geom,
	       stop_times_arrival_time
	FROM snippets
	WHERE calendar_""" +d+ """ AND stop_times_arrival_time >= """ + boundaryFrom + """ AND stop_times_arrival_time < """ + boundaryTo + """;

	CREATE INDEX """ + tname +"""_idx ON """ + tname + """ USING gist (geom);"""
