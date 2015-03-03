'''
See http://flask.pocoo.org/docs/patterns/appfactories/
'''

from bugsnag.flask import handle_exceptions
from flask import Flask
import bugsnag

'''
Flask app creation
'''

def create_app(config):
    app = Flask(__name__)
    app.config.update(config.FLASK)

    # Bugsnag
    bugsnag.configure(**config.BUGSNAG)
    handle_exceptions(app)

    return app
