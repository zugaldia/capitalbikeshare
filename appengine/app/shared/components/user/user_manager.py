'''
Here we wrap any operations that go beyond the UserModel. For example,
launching tasks when users get added/deleted, or the process to
reset/change a password. This logic could change from app to app.
'''

from appython.components.user.base_manager import BaseManager
from shared.components.queue.queue_manager import QueueManager
from shared.config import Config
from shared.models.user_model import UserModel

import logging


class UserManager(BaseManager):
    
    @classmethod
    def create_from_email(cls, email, password):
        # First we check if the user already exists. (This method is safe to
        # call even on existing users, it won't create duplicates.)
        user_model = UserModel.get_by_email(email=email)
        if user_model:
            return False

        # Is admin?
        email_ready = cls.prepare_email(email=email)
        is_admin = (email_ready == Config.SUPERUSER_EMAIL)

        # Create the user
        user_model = UserModel.create_from_email(email=email, password=password)
        user_model.is_admin = is_admin
        user_model.api_key = cls.generate_api_key()
        user_model.put()

        # Launch the welcome task
        QueueManager.launch_user_registration(user_id=user_model.get_id())

        # Done
        return user_model
