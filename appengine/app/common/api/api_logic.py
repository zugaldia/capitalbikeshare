'''
A decorator for all API methods
'''

from common.api.api_exception import ApiException
from common.components.bugsnag.bugsnag_client import BugsnagClient
from werkzeug.exceptions import BadRequest

import logging


def api_logic():
    def dec(func):
        def f2(*args, **kwargs):
            try:
                # Proceed
                return func(*args, **kwargs)
            except ApiException as e:
                # Messages for the user, no need to send them to Bugsnag
                args[0].set_code(code=400)
                args[0].add_error_message(text=str(e))
                return args[0].get_response()
            except BadRequest as e:
                # When parsing fails, the error isn't great:
                # http://stackoverflow.com/questions/14007228/flask-ext-restful-reqparse
                logging.exception(e)
                args[0].set_code(code=400)
                args[0].add_error_message(
                    text='Make sure you included all the required fields (%s).'
                    % str(e))
                return args[0].get_response()
            except Exception as e:
                # Wraps the method in a bugsnag controlled try/except
                BugsnagClient().notify(e, context=func.__name__)
                args[0].set_code(code=500)
                args[0].add_error_message(text='Request failed: %s' % str(e))
                return args[0].get_response()
        return f2
    return dec
