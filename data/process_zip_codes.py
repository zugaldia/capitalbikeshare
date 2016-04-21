'''
Source:
	http://opendata.dc.gov/datasets/5637d4bb43a34668b19fe630120d2b70_4
	http://opendata.dc.gov/datasets/5637d4bb43a34668b19fe630120d2b70_4.geojson
'''

import json
from shapely.geometry import asShape

GEOJSON = '5637d4bb43a34668b19fe630120d2b70_4.geojson'

print 'Reading...'

with open(GEOJSON, 'r') as data_file:    
	data = json.load(data_file)

zipcodes = {}
for feature in data.get('features'):
	zipcode = feature.get('properties').get('ZIPCODE')
	geometry = feature.get('geometry')
	shape = asShape(geometry)
	zipcodes[zipcode] = {
		'lat': shape.centroid.x,
		'lon': shape.centroid.y}

with open('zipcodes.json', 'w') as data_file:    
	json.dump(zipcodes, data_file, indent=True)

print 'Done.'
