from code.blueprint_webapp import blueprint_webapp
from flask import g
from google.appengine.api import app_identity
from shared.bootstrap import create_app
from shared.config import Config, PROJECT_BUILD, PROJECT_STAGE

# import logging

'''
Flask set up
'''

app = create_app(config_flask=Config.FLASK)

# Avoids template tags clashing with AngularJS
app.jinja_options = {
    'variable_start_string': '[[',
    'variable_end_string': ']]'
}

# Send a few vars to JS
@app.before_request
def before_request():
    g.PROJECT_BUILD = PROJECT_BUILD
    g.PROJECT_STAGE = PROJECT_STAGE
    g.HOSTNAME = app_identity.get_default_version_hostname()
    g.API_BASE = Config.API_BASE


'''
Flask blueprints
'''

app.register_blueprint(blueprint_webapp)
