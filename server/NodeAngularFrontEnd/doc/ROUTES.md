
## ROUTES

### Trip Routes

| Operation | HTTP   | SQL    | Resource
| :-------- | :----- | :----- | :--------
| index     | GET    | SELECT | /trips
| show      | GET    | SELECT | /trips/:id
| update    | PUT    | UPDATE | /trips/:id
| destroy   | DELETE | DELETE | /trips/:id
|           | GET    | SELECT | /trips/:id/:sensor
|           | GET    | SELECT | /trips/:id/window/:sensor

Note: `:sensor` must be one of the following:

* `acc` (accelerometer)
* `gps` (gps)
* `lac` (linear_acceleration)
* `gra` (gravity)

* `actg` (google_activity)
* `act` (activity)

* `tag` (tags)

(Yes, the last three ones are actually not sensors...)

#### Show all trips

	curl http://localhost:3000/trips

#### Show a specific trip

	curl http://localhost:3000/trips/42

#### Update a trip

	curl -X PUT -H "Content-Type: application/json" \
		-d '{ "description": "asdf", "user_id" : "qwer" }' localhost:3000/trip/42

#### Delete a trip

	 curl -X DELETE localhost:3000/trips/42

### Device Routes

| Operation | HTTP   | SQL    | Resource
| :-------- | :----- | :----- | :--------
| index     | GET    | SELECT | /devices
| show      | GET    | SELECT | /devices/:user_id
|           | GET    | SELECT | /devices/:user_id/:sensor
|           | GET    | SELECT | /devices/:user_id/count

Note: :sensor must be one of `gra` (gravity), `lac` (linear_acceleration) or `acc` (acceleration).

### Frontend Routes (Angular)

| Operation | HTTP   | SQL    | Resource
| :-------- | :----- | :----- | :--------
|           | GET    | SELECT | /partials/:name
|           | GET    | SELECT | /

### CSV

You can append `.csv` to any route to get CSV instead of JSON.
