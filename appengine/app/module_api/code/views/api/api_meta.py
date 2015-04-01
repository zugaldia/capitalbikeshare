from appython.components.api.api_auth import api_auth
from appython.components.api.api_exception import ApiException
from appython.components.api.base_api import BaseApi
from appython.components.api.common_requests import bool_request
from appython.components.api.common_requests import string_request
from appython.components.api.common_responses import StringResponse
from datetime import datetime
from flask_login import current_user
from google.appengine.api import app_identity
from google.appengine.api import capabilities
from shared.config import PROJECT_BUILD, PROJECT_STAGE
from shared.components.api.api_sentry import api_sentry

class ApiMeta(BaseApi):

    '''
    Basic system info
    '''

    # /api/v1/meta/info?flag=true
    @api_sentry
    def get_info(self):
        args = bool_request.parse_args()
        include_capabilities = args.get('flag')

        # URLs
        hostname = app_identity.get_default_version_hostname()
        api_root = 'https://api-dot-%s' % hostname

        # Basic
        data = {
            'now': datetime.now().isoformat(),
            'build': PROJECT_BUILD,
            'stage': PROJECT_STAGE,
            'hostname': hostname,
            'api_root': api_root}

        # Capabilities API
        if include_capabilities:
            data.update({
                'blobstore_ok': capabilities.CapabilitySet('blobstore').is_enabled(),
                'datastore_ok': capabilities.CapabilitySet('datastore_v3').is_enabled(),
                'images_ok': capabilities.CapabilitySet('images').is_enabled(),
                'mail_ok': capabilities.CapabilitySet('mail').is_enabled(),
                'memcache_ok': capabilities.CapabilitySet('memcache').is_enabled(),
                'taskqueue_ok': capabilities.CapabilitySet('taskqueue').is_enabled(),
                'urlfetch_ok': capabilities.CapabilitySet('urlfetch').is_enabled(),
                'xmpp_ok': capabilities.CapabilitySet('xmpp').is_enabled()})

        self.set_data(data=data)
        return self.get_response()

    '''
    Test request and response models
    '''

    # /api/v1/meta/echo?text=foo
    @api_sentry
    def get_echo(self):
        args = string_request.parse_args()
        text = args.get('text') or '(empty)'
        data = StringResponse(text=text).to_api()
        self.set_data(data=data)
        return self.get_response()

    '''
    Test errors
    '''

    # /api/v1/meta/check_error
    @api_sentry
    def get_check_error(self):
        raise ApiException('You trusted me, and I failed you.')

    # /api/v1/meta/check_sentry
    @api_sentry
    def get_check_sentry(self):
        # ApiException aren't sent to Sentry by default, other exceptions are.
        raise Exception('Hello Sentry!')

    '''
    Test credentials
    '''

    # /api/v1/meta/check_login
    @api_sentry
    @api_auth(login_required=True)
    def get_check_login(self):
        self.set_data(data={'me': current_user.to_api()})
        return self.get_response()

    # /api/v1/meta/check_admin
    @api_sentry
    @api_auth(admin_required=True)
    def get_check_admin(self):
        self.set_data(data={'me': current_user.to_api()})
        return self.get_response()
