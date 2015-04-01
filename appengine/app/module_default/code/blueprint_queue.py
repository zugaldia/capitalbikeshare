'''
Task queue
'''

from code.views.queue.user_registration import UserRegistrationView
from flask import Blueprint

blueprint_queue = Blueprint('queue', __name__)

blueprint_queue.add_url_rule(
    '/_ah/queue/user-registration',
    view_func=UserRegistrationView.as_view('user_registration'))
