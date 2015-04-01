from flask.ext.restful import reqparse

'''
User endpoint
'''

setup_request = reqparse.RequestParser()
setup_request.add_argument('password', type=unicode)

'''
Data
'''

status_feed_request = reqparse.RequestParser()
status_feed_request.add_argument('latitude', type=float, required=False)
status_feed_request.add_argument('longitude', type=float, required=False)

status_request = reqparse.RequestParser()
status_request.add_argument('latitude', type=float, required=True)
status_request.add_argument('longitude', type=float, required=True)

closest_bike_request = reqparse.RequestParser()
closest_bike_request.add_argument('latitude', type=float, required=True)
closest_bike_request.add_argument('longitude', type=float, required=True)

closest_dock_request = reqparse.RequestParser()
closest_dock_request.add_argument('latitude', type=float, required=True)
closest_dock_request.add_argument('longitude', type=float, required=True)
