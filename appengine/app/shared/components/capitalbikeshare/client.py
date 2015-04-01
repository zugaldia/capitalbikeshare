from google.appengine.api import memcache
from google.appengine.api import urlfetch
from math import radians, cos, sin, asin, sqrt
from operator import itemgetter
import xml.etree.ElementTree as ET

import logging

MEMCACHE_KEY = 'status_feed_xml'
MEMCACHE_TIME = 60 * 5  # 5 minutes
MEMCACHE_NAMESPACE = 'capitalbikeshare'

STATUS_FEED_XML = 'https://www.capitalbikeshare.com/data/stations/bikeStations.xml'

class CapitalBikeshareClient(object):
    '''
    API
    '''

    def get_status_feed(self, latitude=None, longitude=None, distance_sort=True):
        status_feed_xml = self._get_status_feed_xml()
        status_feed = self._xml_to_dict(status_feed_xml=status_feed_xml)

        # If a latlon is provided, compute distances
        if latitude is not None and longitude is not None:
            # Compute distances
            for station in status_feed['stations']:
                distance_m = self._compute_distance(
                    from_latitude=station['latitude'],
                    from_longitude=station['longitude'],
                    to_latitude=latitude,
                    to_longitude=longitude)

                # Round and estimate walk (5km/h)
                station['distance_m'] = round(distance_m)
                station['distance_min'] = round(distance_m * 60 / 5000)

            # Sort stations by distance
            if distance_sort:
                status_feed['stations'] = sorted(
                    status_feed['stations'],
                    key=itemgetter('distance_m'))

        return status_feed

    '''
    URL Fetch logic
    '''

    def _get_status_feed_xml(self):
        status_feed_xml = self._cache_get()

        # Not in cache
        if status_feed_xml is None:
            response = urlfetch.fetch(url=STATUS_FEED_XML)
            if response.status_code == 200:
                status_feed_xml = response.content
                self._cache_set(status_feed_xml=status_feed_xml)

        return status_feed_xml

    '''
    Memcache logic
    '''

    def _cache_get(self):
        return memcache.get(
            key=MEMCACHE_KEY,
            namespace=MEMCACHE_NAMESPACE)

    def _cache_set(self, status_feed_xml):
        return memcache.set(
            key=MEMCACHE_KEY,
            value=status_feed_xml,
            time=MEMCACHE_TIME,
            namespace=MEMCACHE_NAMESPACE)

    '''
    XML processing logic

    The structure of the XML document is quite simple and as follows:

        <?xml version="1.0" encoding="UTF-8"?>
        <stations lastUpdate="1425419865535" version="2.0">
            <station>
                <id>1</id>
                <name>20th &amp; Bell St</name>
                <terminalName>31000</terminalName>
                <lastCommWithServer>1425386803785</lastCommWithServer>
                <lat>38.8561</lat>
                <long>-77.0512</long>
                <installed>true</installed>
                <locked>false</locked>
                <installDate>0</installDate>
                <removalDate/>
                <temporary>false</temporary>
                <public>true</public>
                <nbBikes>6</nbBikes>
                <nbEmptyDocks>5</nbEmptyDocks>
                <latestUpdateTime>1425386803785</latestUpdateTime>
            </station>
            ...
        </stations>

    '''

    def _xml_to_dict(self, status_feed_xml):
        # Structure
        status_feed = {
            'last_update': None,
            'version': None,
            'stations': []}

        # Parse the actual XML
        root = ET.fromstring(text=status_feed_xml)

        # Get the root attributes
        status_feed['last_update'] = root.attrib.get('lastUpdate')
        status_feed['version'] = root.attrib.get('version')

        # Go through the stations
        for child in root:
            station = {
                'id': self._get_element_text(
                    element=child.find('id')),
                'name': self._get_element_text(
                    element=child.find('name')),
                'terminal_name': self._get_element_text(
                    element=child.find('terminalName')),
                'last_comm_with_server': self._get_element_text(
                    element=child.find('lastCommWithServer')),
                'latitude': self._get_element_text(
                    element=child.find('lat')),
                'longitude': self._get_element_text(
                    element=child.find('long')),
                'installed': self._get_element_text(
                    element=child.find('installed')),
                'locked': self._get_element_text(
                    element=child.find('locked')),
                'install_date': self._get_element_text(
                    element=child.find('installDate')),
                'removal_date': self._get_element_text(
                    element=child.find('removalDate')),
                'temporary': self._get_element_text(
                    element=child.find('temporary')),
                'public': self._get_element_text(
                    element=child.find('public')),
                'nb_bikes': self._get_element_text(
                    element=child.find('nbBikes')),
                'nb_empty_docks': self._get_element_text(
                    element=child.find('nbEmptyDocks')),
                'latest_update_time': self._get_element_text(
                    element=child.find('latestUpdateTime'))}

            # A few data conversions
            station['latitude'] = float(station['latitude'])
            station['longitude'] = float(station['longitude'])
            station['nb_bikes'] = int(station['nb_bikes'])
            station['nb_empty_docks'] = int(station['nb_empty_docks'])

            # Done
            status_feed['stations'].append(station)

        return status_feed

    def _get_element_text(self, element):
        if element is not None:
            return element.text

    '''
    Distance logic
    See: http://stackoverflow.com/questions/4913349/haversine-formula-in-python-bearing-and-distance-between-two-gps-points
    '''

    def _compute_distance(self, from_latitude, from_longitude, to_latitude, to_longitude):
        # Convert decimal degrees to radians
        from_longitude, from_latitude, to_longitude, to_latitude = map(
            radians, [from_longitude, from_latitude, to_longitude, to_latitude])

        # Haversine formula 
        dlon = to_longitude - from_longitude 
        dlat = to_latitude - from_latitude 
        a = sin(dlat/2)**2 + cos(from_latitude) * cos(to_latitude) * sin(dlon/2)**2
        c = 2 * asin(sqrt(a)) 
        r = 6371000  # Radius of earth in meters

        return c * r
