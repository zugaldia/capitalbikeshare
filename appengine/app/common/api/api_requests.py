from flask.ext.restful.reqparse import RequestParser

'''
Meta
'''

echo_request = RequestParser()
echo_request.add_argument('echo', type=str, required=True)

'''
Data
'''

status_request = RequestParser()
status_request.add_argument('echo', type=str, required=True)

closest_bike_request = RequestParser()
closest_bike_request.add_argument('echo', type=str, required=True)

closest_dock_request = RequestParser()
closest_dock_request.add_argument('echo', type=str, required=True)
