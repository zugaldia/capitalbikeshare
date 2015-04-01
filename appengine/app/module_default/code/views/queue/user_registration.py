from appython.components.queue.base_queue import BaseQueue
from shared.components.sentry import sentry_client
from google.appengine.ext import ndb

import logging


class UserRegistrationView(BaseQueue):

    def post(self):
        try:
            assert self._is_legit()
            self._initialize_data()
            self.execute()
        except:
            # In development, this will only be logged
            sentry_client.capture_exception()
        finally:
            return 'Done.'

    def execute(self):
        # Your code goes here
        logging.info('Welcome onboard!')
        logging.info(self.data)

        # How to get the user instance
        urlsafe = self.data.get('user_id')
        user_key = ndb.Key(urlsafe=urlsafe)
        user_model = user_key.get()
        logging.info(user_model)
