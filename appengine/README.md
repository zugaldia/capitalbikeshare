# Capital Bikeshare API

This app provides a JSON API to the Capital Bikeshare data which is officially [available in XML format](https://www.capitalbikeshare.com/data/stations/bikeStations.xml).

We provide four GET methods described below.

## Get status feed

Provides the same information as the XML file mentioned above, but in JSON format.

* Sample request: [/api/v1/data/status_feed](https://api-dot-com-zugaldia-capitalbikeshare.appspot.com/api/v1/data/status_feed)

* Sample response:

```

{
    "code": 200, 
    "messages": [], 
    "status_feed": {
        "last_update": "1425490067575", 
        "stations": [
            {
                "id": "1", 
                "install_date": "0", 
                "installed": "true", 
                "last_comm_with_server": "1425463262234", 
                "latest_update_time": "1425463262234", 
                "latitude": 38.856099999999998, 
                "locked": "false", 
                "longitude": -77.051199999999994, 
                "name": "20th & Bell St", 
                "nb_bikes": 7, 
                "nb_empty_docks": 4, 
                "public": "true", 
                "removal_date": null, 
                "temporary": "false", 
                "terminal_name": "31000"
            }, 
            {
                "id": "2", 
                "install_date": "0", 
                "installed": "true", 
                "last_comm_with_server": "1425463277041", 
                "latest_update_time": "1425463277041", 
                "latitude": 38.857250000000001, 
                "locked": "false", 
                "longitude": -77.053319999999999, 
                "name": "18th & Eads St.", 
                "nb_bikes": 8, 
                "nb_empty_docks": 3, 
                "public": "true", 
                "removal_date": null, 
                "temporary": "false", 
                "terminal_name": "31001"
            }, 
            ...
        ], 
        "version": "2.0"
    }
}
```

Optionally, you can add `latitude` and `longitude` fields (which provides an enhancement over the original XML API). In this case, the API will compute the distance (in meters and minutes) to that point and it'll sort the stations by distance.

* Sample request (White House): [/api/v1/data/status_feed?latitude=38.8977&longitude=-77.0365](https://api-dot-com-zugaldia-capitalbikeshare.appspot.com/api/v1/data/status_feed?latitude=38.8977&longitude=-77.0365)

* Sample response:

```

{
    "code": 200, 
    "messages": [], 
    "status_feed": {
        "last_update": "1425490067575", 
        "stations": [
            {
                "distance_m": 282.0, 
                "distance_min": 3.0, 
                "id": "349", 
                "install_date": "0", 
                "installed": "true", 
                "last_comm_with_server": "1425489621494", 
                "latest_update_time": "1425489621494", 
                "latitude": 38.898409999999998, 
                "locked": "false", 
                "longitude": -77.039624000000003, 
                "name": "17th & G St NW", 
                "nb_bikes": 25, 
                "nb_empty_docks": 6, 
                "public": "true", 
                "removal_date": null, 
                "temporary": "false", 
                "terminal_name": "31277"
            }, 
            {
                "distance_m": 288.0, 
                "distance_min": 3.0, 
                "id": "83", 
                "install_date": "0", 
                "installed": "true", 
                "last_comm_with_server": "1425490010031", 
                "latest_update_time": "1425490010031", 
                "latitude": 38.899099999999997, 
                "locked": "false", 
                "longitude": -77.033699999999996, 
                "name": "New York Ave & 15th St NW", 
                "nb_bikes": 12, 
                "nb_empty_docks": 7, 
                "public": "true", 
                "removal_date": null, 
                "temporary": "false", 
                "terminal_name": "31222"
            }, 
            ...
        ], 
        "version": "2.0"
    }
}

```

## Get status

Gives a summary of the stations situation around you. This gives the user an idea of how easy/difficult is going to be to find a bike/dock around.

* Sample request (Dupont Circle): [/api/v1/data/status?latitude=38.90962&longitude=-77.04341](https://api-dot-com-zugaldia-capitalbikeshare.appspot.com/api/v1/data/status?latitude=38.90962&longitude=-77.04341)

* Sample response:

```
{
    "code": 200, 
    "messages": [], 
    "status": {
        "bikes": 58, 
        "docks": 60, 
        "stations": 5
    }
}
```

In this method, both the `latitude` and `longitude` fields are required. Stations farther than 500 meters (a 5-6 minutes walk) are considered "too far" and are not included in the result.

## Get closest bike

Finds the closest bike available to a given location.

* Sample request (Washington Monument): [/api/v1/data/closest_bike?latitude=38.889468&longitude=-77.03524](https://api-dot-com-zugaldia-capitalbikeshare.appspot.com/api/v1/data/closest_bike?latitude=38.889468&longitude=-77.03524)

* Sample response:

```
{
    "code": 200, 
    "messages": [], 
    "station": {
        "distance_m": 264.0, 
        "distance_min": 3.0, 
        "id": "168", 
        "install_date": "0", 
        "installed": "true", 
        "last_comm_with_server": "1425489678910", 
        "latest_update_time": "1425489678910", 
        "latitude": 38.888553000000002, 
        "locked": "false", 
        "longitude": -77.032428999999993, 
        "name": "Jefferson Dr & 14th St SW", 
        "nb_bikes": 9, 
        "nb_empty_docks": 14, 
        "public": "true", 
        "removal_date": null, 
        "temporary": "false", 
        "terminal_name": "31247"
    }
}
```

In this method, both the `latitude` and `longitude` fields are required. Stations farther than 500 meters (a 5-6 minutes walk) are considered "too far" and are not included in the result.

## Get closest dock

Finds the closest dock available to a given location.

* Sample request (Starbucks, U St NW): [/api/v1/data/closest_dock?latitude=38.9168297&longitude=-77.0370018](https://api-dot-com-zugaldia-capitalbikeshare.appspot.com/api/v1/data/closest_dock?latitude=38.9168297&longitude=-77.0370018)

* Sample response:

```
{
    "code": 200, 
    "messages": [], 
    "station": {
        "distance_m": 176.0, 
        "distance_min": 2.0, 
        "id": "102", 
        "install_date": "0", 
        "installed": "true", 
        "last_comm_with_server": "1425489825962", 
        "latest_update_time": "1425489825962", 
        "latitude": 38.91554, 
        "locked": "false", 
        "longitude": -77.038179999999997, 
        "name": "New Hampshire Ave & T St NW", 
        "nb_bikes": 4, 
        "nb_empty_docks": 18, 
        "public": "true", 
        "removal_date": null, 
        "temporary": "false", 
        "terminal_name": "31229"
    }
}
```

In this method, both the `latitude` and `longitude` fields are required. Stations farther than 500 meters (a 5-6 minutes walk) are considered "too far" and are not included in the result.
