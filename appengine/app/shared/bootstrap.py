'''
See http://flask.pocoo.org/docs/patterns/appfactories/
'''

from appython.components.user.user_callback import user_callback
from flask import Flask
from flask_login import LoginManager
from shared.components.sentry import sentry_client
from shared.components.user.request_callback import request_callback

# import logging


def create_app(config_flask):
    app = Flask(__name__)
    app.config.update(config_flask)

    # Flask-Login
    login_manager = LoginManager()
    login_manager.init_app(app=app)
    login_manager.session_protection = 'strong'
    login_manager.user_callback = user_callback
    login_manager.request_callback = request_callback

    # Sentry client
    sentry_client.init_app(app=app)
    return app
