'''
See: https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/alexa-skills-kit-interface-reference
'''

from appython.components.api.api_exception import ApiException
from appython.components.api.base_api import BaseApi
from appython.components.api.common_requests import string_request
from appython.components.api.common_responses import StringResponse
from flask import request
from shared.components.api.api_sentry import api_sentry
from shared.components.capitalbikeshare.client import CapitalBikeshareClient
import cgi

import logging

# More than a kilometer is considered too far
CUTOFF_DISTANCE = 1000

APPLICATION_ID = 'amzn1.echo-sdk-ams.app.7d501f24-e164-4396-a017-7afb7919f70b'


class ApiAlexa(BaseApi):

    '''
    Test request and response models
    '''

    # /api/v1/alexa/echo?text=foo
    @api_sentry
    def get_echo(self):
        args = string_request.parse_args()
        text = args.get('text') or '(empty)'
        data = StringResponse(text=text).to_api()
        self.set_data(data=data)
        return self.get_response()

    '''
    Main skill router
    '''

    # /api/v1/alexa/skill
    @api_sentry
    def post_skill(self):
        decoded = request.get_json()

        # Check app ID
        application_id = decoded.get('session').get('application').get('applicationId')
        if application_id != APPLICATION_ID:
            raise ApiException('Uh uh uh! You didn\'t say the magic word!')

        # Route requests
        request_type = decoded.get('request').get('type')
        if request_type == 'LaunchRequest':
            return self.handle_launch_request()
        elif request_type == 'IntentRequest':
            return self.handle_intent_request()
        elif request_type == 'SessionEndedRequest':
            return self.handle_session_ended_request()
        else:
            raise ApiException('Unknown request type: %s' % request_type)

    '''
    Types of Requests Sent by Alexa
    '''

    def _get_response_template(self):
        return {
            'version': '1.0',
            'response': {
                'outputSpeech': {
                    'type': 'PlainText',
                    'text': 'TODO'
                },
                'card': {
                    'type': 'Simple',
                    'title': 'Capital Bikeshare',
                    'content': 'TODO',
                },
                'shouldEndSession': True
            }
        }

    def handle_launch_request(self):
        return self._handle_nearby_intent()

    def handle_intent_request(self):
        decoded = request.get_json()
        intent_name = decoded.get('request').get('intent').get('name')
        if intent_name == 'NearbyIntent':
            return self._handle_nearby_intent()
        else:
            raise ApiException('Unknown intent name: %s' % intent_name)

    def _handle_nearby_intent(self):
        # Soho
        latitude = 38.909601
        longitude = -77.048393

        # Default object
        target_station = None

        # Get the CapitalBikeshareClient
        client = CapitalBikeshareClient()
        status_feed = client.get_status_feed(
                latitude=latitude,
                longitude=longitude,
                distance_sort=True)
        for station in status_feed['stations']:
            if station['distance_m'] <= CUTOFF_DISTANCE and station['nb_bikes'] > 0:
                target_station = station
                break

        # Build message
        if target_station is None:
            message = cgi.escape('Sorry, we couldn\'t find any available bikes nearby.')
        else:
            message = cgi.escape('We\'ve found %d bikes on %s, that\'s about a %d minutes walk.' % (
                target_station.get('nb_bikes'),
                target_station.get('name'),
                target_station.get('distance_min')))

        # Build response
        response = self._get_response_template()
        response['response']['outputSpeech']['text'] = message
        response['response']['card']['content'] = message
        return response

    def handle_session_ended_request(self):
        return {
            'version': '1.0',
            'response': {
                'shouldEndSession': True
            }
        }
