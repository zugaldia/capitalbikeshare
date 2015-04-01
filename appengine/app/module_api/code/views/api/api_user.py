from appython.components.api.api_auth import api_auth
from appython.components.api.api_exception import ApiException
from appython.components.api.base_api import BaseApi
from flask import request
from shared.components.api.api_requests import setup_request
from shared.components.api.api_sentry import api_sentry
from shared.components.user.user_manager import UserManager
from shared.config import Config

import logging

class ApiUser(BaseApi):

    '''
    First time setup. It's safe to call this method several times (if there's
    nothing to do, it'll do nothing). If we don't provide a password for the
    superuser, we generate one.
    '''

    # /api/v1/user/setup?password=xxx
    @api_sentry
    def get_setup(self):
        # Default
        data = {'created': False}

        # SSL only (except development)
        if (Config.IS_PRODUCTION and not request.url.startswith('https')):
            raise ApiException('Please run this method in secure mode (HTTPS).')
        
        # Password
        args = setup_request.parse_args()
        password = args.get('password') or UserManager.generate_password()

        # Creates the account for the superuser if it doesn't exist yet, and
        # launches the welcome task.
        user_model = UserManager.create_from_email(
            email=Config.SUPERUSER_EMAIL,
            password=password)

        # Add the credentials if we just created the user (this only happens
        # the first time).
        if user_model:
            data.update({
                'created': True,
                'password': password,
                'api_key': user_model.api_key,
                'user': user_model.to_api()})

        self.set_data(data=data)
        return self.get_response()
