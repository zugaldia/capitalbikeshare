from appython.components.cron.base_cron import BaseCron
from shared.components.sentry import sentry_client

import logging


class CronDailyView(BaseCron):
    def get(self):
        try:
            assert self._is_legit()
            self.execute()
        except:
            # In development, this will only be logged
            sentry_client.capture_exception()
        finally:
            return 'Done.'

    def execute(self):
        # Your code goes here
        logging.info('I have fun running every 24 hours.')
