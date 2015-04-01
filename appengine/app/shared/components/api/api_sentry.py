'''
We should use this decorator for all API methods, although it's optional. It
makes sure that exceptions raised by the method are properly displayed to
the user, and reported to Sentry when necessary.
'''

from appython.components.api.api_exception import ApiException
from appython.components.api.api_status import ApiStatus
from functools import wraps
from shared.components.sentry import sentry_client
from werkzeug.exceptions import BadRequest

# import logging

def api_sentry(func):
    @wraps(func)
    def wrapper(*args, **kwds):
        try:
            # Proceed
            return func(*args, **kwds)
        except ApiException as e:
            # These exceptions are manually created by the API methods as
            # a controlled message to the user. They don't need to be
            # reported to Sentry.
            args[0].set_code(code=ApiStatus.INTERNAL_SERVER_ERROR)
            args[0].set_message(message=str(e))
            return args[0].get_response()
        except BadRequest as e:
            # When parsing fails (for example, when missing a required=True
            # parameter), the error isn't great:
            # http://stackoverflow.com/questions/14007228/flask-ext-restful-reqparse
            sentry_client.capture_exception()
            args[0].set_code(code=ApiStatus.BAD_REQUEST)
            args[0].set_message(
                message='Make sure you included all the required fields (%s).'
                % str(e))
            return args[0].get_response()
        except Exception as e:
            # Everything else
            sentry_client.capture_exception()
            args[0].set_code(code=ApiStatus.INTERNAL_SERVER_ERROR)
            args[0].set_message(message='Unexpected error: %s' % str(e))
            return args[0].get_response()
    return wrapper
