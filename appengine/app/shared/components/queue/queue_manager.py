from appython.components.queue.base_manager import BaseManager
from shared.config import Config
import json

import logging


class QueueManager(BaseManager):

    @classmethod
    def launch_user_registration(cls, user_id):
        data = {'user_id': user_id}
        if Config.IS_DEVELOPMENT:
            logging.info('launch_user_registration: %s' % json.dumps(data))
            return
        cls.launch_task(queue_name='user-registration', data=data)
