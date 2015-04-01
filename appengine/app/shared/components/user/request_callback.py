'''
This sets the callback for loading a user from a Flask request. The function
you set should take Flask request object and return a user object, or None
if the user does not exist.

See: https://flask-login.readthedocs.org/en/latest/#custom-login-using-request-loader
'''

from shared.models.user_model import UserModel
import base64


def request_callback(request):
    # First, try to login using the api_key url arg
    api_key = request.args.get('api_key')
    if api_key:
        user_model = UserModel.get_by_api_key(api_key=api_key)
        if user_model:
            return user_model

    # Next, try to login using Basic Auth
    api_key = request.headers.get('Authorization')
    if api_key:
        api_key = api_key.replace('Basic ', '', 1)
        try:
            api_key = base64.b64decode(api_key)
        except TypeError:
            pass
        user_model = UserModel.get_by_api_key(api_key=api_key)
        if user_model:
            return user_model

    # Finally, return None if both methods did not login the user
    return None
