'''
See http://flask.pocoo.org/docs/blueprints/
'''

from code.views.alexa.api_alexa import ApiAlexa
from flask import Blueprint
from flask.ext import restful
from shared.config import Config

blueprint_alexa = Blueprint('alexa', __name__)

'''
The API
'''

api = restful.Api(blueprint_alexa)
api.add_resource(ApiAlexa, Config.API_BASE + '/alexa/<action>')
