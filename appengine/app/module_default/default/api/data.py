from common.api.api_logic import api_logic
from common.api.api_requests import closest_bike_request
from common.api.api_requests import closest_dock_request
from common.api.api_requests import status_feed_request
from common.api.api_requests import status_request
from common.api.base_resource import BaseResource
from common.components.capitalbikeshare.client import CapitalBikeshareClient
from flask.ext.restful import fields, marshal

import logging

# More than half a kilometer is considered too far
# That's about a 5-6 minutes walk
CUTOFF_DISTANCE = 500

client = CapitalBikeshareClient()

class ApiData(BaseResource):

    '''
    Provides a full feed of data in JSON format, if a latlon is provided it
    will also calculate the distances and sort the stations by distance.

    For example:
        /api/v1/data/status_feed
        /api/v1/data/status_feed?latitude=38.90962&longitude=-77.04341 (dupont circle)
        /api/v1/data/status_feed?latitude=38.8977&longitude=-77.0365 (white house)
    '''

    @api_logic()
    def get_status_feed(self):
        # Parse args
        args = status_feed_request.parse_args()
        latitude = args.get('latitude')
        longitude = args.get('longitude')

        # Get the data
        status_feed = client.get_status_feed(
            latitude=latitude, longitude=longitude, distance_sort=True)

        # Done
        self.set_data(data={'status_feed': status_feed})
        return self.get_response()

    '''
    Provides an status of the situation nearby (how easy or how difficult is
    gonna be to get a bike/dock)
    '''

    @api_logic()
    def get_status(self):
        # Parse args
        args = status_request.parse_args()
        latitude = args.get('latitude')
        longitude = args.get('longitude')

        # Default object
        status = {'stations': 0, 'bikes': 0, 'docks': 0}

        # Process the data
        status_feed = client.get_status_feed(
            latitude=latitude, longitude=longitude, distance_sort=True)
        for station in status_feed['stations']:
            if station['distance_m'] <= CUTOFF_DISTANCE:
                status['stations'] += 1
                status['bikes'] += station['nb_bikes']
                status['docks'] += station['nb_empty_docks']
            else:
                # We know the stations are ordered by distance
                break

        # Done
        self.set_data(data={'status': status})
        return self.get_response()

    '''
    "Find me a bike"
    '''

    @api_logic()
    def get_closest_bike(self):
        # Parse args
        args = closest_bike_request.parse_args()
        latitude = args.get('latitude')
        longitude = args.get('longitude')

        # Default object
        target_station = {}

        # Process the data
        status_feed = client.get_status_feed(
            latitude=latitude, longitude=longitude, distance_sort=True)
        for station in status_feed['stations']:
            if station['distance_m'] <= CUTOFF_DISTANCE and station['nb_bikes'] > 0:
                target_station = station
                break

        # Done
        self.set_data(data={'station': target_station})
        return self.get_response()

    '''
    "Find me a dock"
    '''

    @api_logic()
    def get_closest_dock(self):
        # Parse args
        args = closest_dock_request.parse_args()
        latitude = args.get('latitude')
        longitude = args.get('longitude')

        # Default object
        target_station = {}

        # Process the data
        status_feed = client.get_status_feed(
            latitude=latitude, longitude=longitude, distance_sort=True)
        for station in status_feed['stations']:
            if station['distance_m'] <= CUTOFF_DISTANCE and station['nb_empty_docks'] > 0:
                target_station = station
                break

        # Done
        self.set_data(data={'station': target_station})
        return self.get_response()
