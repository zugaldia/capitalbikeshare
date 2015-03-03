from capitalbikeshare.api.api_logic import api_logic
from capitalbikeshare.api.api_requests import echo_request
from capitalbikeshare.api.base_resource import BaseResource
from flask.ext.restful import fields, marshal

import logging

class ApiData(BaseResource):

    @api_logic()
    def get_echo(self):
        return self.get_response()
