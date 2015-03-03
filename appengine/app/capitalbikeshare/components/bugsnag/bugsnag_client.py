import bugsnag
import json

import logging

class BugsnagClient(object):

    def __init__(self, dev_mode=False):
        self._dev_mode = dev_mode

    def notify(self, exception, context='no_context', meta_data={}):
        # Unhandled exceptions are automatically sent to Bugsnag by the
        # notifier. We only use this method to send handled exceptions to
        # Bugsnag, and to the logger if we're in development mode.
        if self._dev_mode:
            logging.info('BugsnagClient: {} {}\n'.format(context, json.dumps(meta_data)))
            logging.exception(exception)
            return

        # Send to Bugsnag
        bugsnag.notify(
            exception=exception,
            context=context,
            meta_data=meta_data)
