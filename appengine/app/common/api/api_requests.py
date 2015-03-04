from flask.ext.restful.reqparse import RequestParser

'''
Meta
'''

echo_request = RequestParser()
echo_request.add_argument('echo', type=str, required=True)

'''
Data
'''

status_feed_request = RequestParser()
status_feed_request.add_argument('latitude', type=float, required=False)
status_feed_request.add_argument('longitude', type=float, required=False)

status_request = RequestParser()
status_request.add_argument('latitude', type=float, required=True)
status_request.add_argument('longitude', type=float, required=True)

closest_bike_request = RequestParser()
closest_bike_request.add_argument('latitude', type=float, required=True)
closest_bike_request.add_argument('longitude', type=float, required=True)

closest_dock_request = RequestParser()
closest_dock_request.add_argument('latitude', type=float, required=True)
closest_dock_request.add_argument('longitude', type=float, required=True)
