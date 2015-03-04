from common.api.api_logic import api_logic
from common.api.api_requests import closest_bike_request
from common.api.api_requests import closest_dock_request
from common.api.api_requests import status_request
from common.api.base_resource import BaseResource
from flask.ext.restful import fields, marshal

import logging

class ApiData(BaseResource):

    @api_logic()
    def get_status(self):
        args = status_request.parse_args()
        return self.get_response()

    @api_logic()
    def get_closest_bike(self):
        args = closest_bike_request.parse_args()
        return self.get_response()

    @api_logic()
    def get_closest_dock(self):
        args = closest_dock_request.parse_args()
        return self.get_response()
