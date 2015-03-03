from capitalbikeshare.bootstrap import create_app
from capitalbikeshare.config import Config
from default.api.meta import ApiMeta
from default.blueprints.landing import landing_blueprint
from flask.ext import restful

# import logging

'''
Flask set up
'''

app = create_app(config=Config)

'''
Flask-RESTful
'''

api = restful.Api(app)
api.add_resource(ApiMeta, Config.API_BASE + '/meta/<action>')

'''
Flask blueprints
'''

app.register_blueprint(landing_blueprint)
