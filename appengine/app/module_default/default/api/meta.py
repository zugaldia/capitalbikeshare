from capitalbikeshare.api.api_logic import api_logic
from capitalbikeshare.api.api_requests import echo_request
from capitalbikeshare.api.base_resource import BaseResource
from flask.ext.restful import fields, marshal

import logging


# Sample response object
class EchoModel(object):
    def __init__(self, echo):
        self.echo = echo

    def to_api(self, include_echo=True):
        data_fields = {}
        if include_echo:
            data_fields['echo'] = fields.String
        return marshal(self, data_fields)


class ApiMeta(BaseResource):

    # /api/v1/meta/echo?echo=zeta
    @api_logic()
    def get_echo(self):
        args = echo_request.parse_args()
        echo_model = EchoModel(echo=args.get('echo'))
        self.set_data(data=echo_model.to_api(include_echo=True))
        return self.get_response()
