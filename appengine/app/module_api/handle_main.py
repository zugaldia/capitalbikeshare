from code.blueprint_alexa import blueprint_alexa
from code.blueprint_api import blueprint_api
from code.blueprint_webapp import blueprint_webapp
from shared.bootstrap import create_app
from shared.config import Config

# import logging

'''
Flask set up
'''

app = create_app(config_flask=Config.FLASK)

'''
Flask blueprints
'''

app.register_blueprint(blueprint_webapp)
app.register_blueprint(blueprint_api)
app.register_blueprint(blueprint_alexa)
