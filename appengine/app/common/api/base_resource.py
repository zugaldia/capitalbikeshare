from flask import request
from flask.ext.restful import Resource

# import logging


class BaseResource(Resource):
    def __init__(self):
        # Defaults
        self._data = {}
        self._code = 200
        self._messages = []
        self._headers = {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS'}

    '''
    HTTP methods
    '''

    def get(self, action):
        return self._process_method(request_type='get', action=action)

    def post(self, action):
        return self._process_method(request_type='post', action=action)

    def put(self, action):
        return self._process_method(request_type='put', action=action)

    def delete(self, action):
        return self._process_method(request_type='delete', action=action)

    def options(self, action):
        # Required by preflighted requests
        return self.get_response()

    '''
    Process request
    '''

    def _process_method(self, request_type, action):
        # No action
        if not action:
            self.add_error_message(
                text='Please include an action in your request.')
            self.set_code(code=400)
            return self.get_response()

        # All good
        action_name = '%s_%s' % (request_type, action)
        if not action.startswith('_') and hasattr(self, action_name):
            return getattr(self, action_name)()

        # Unknown action
        self.add_error_message(
            text='Unknown action %s (%s) in %s.'
            % (action, request_type.upper(), self.__class__.__name__))
        self.set_code(code=404)
        return self.get_response()

    '''
    Build response
    '''

    def set_data(self, data):
        if not isinstance(data, dict):
            raise Exception('BaseResource: data must be a dict.')
        if ('messages' in data or 'status' in data):
            raise Exception(
                'BaseResource: messages and status are reserved keys.')
        self._data = data

    def set_code(self, code):
        self._code = code

    def add_message(self, text):
        self._messages.append({
            'text': text,
            'severity': 'info'})

    def add_success_message(self, text):
        self._messages.append({
            'text': text,
            'severity': 'success'})

    def add_warning_message(self, text):
        self._messages.append({
            'text': text,
            'severity': 'warning'})

    def add_error_message(self, text):
        self._messages.append({
            'text': text,
            'severity': 'danger'})

    def get_response(self):
        data = self._data
        data.update({'messages': self._messages})
        data.update({'status': self._code})
        return data, self._code, self._headers
