'''
See http://flask.pocoo.org/docs/blueprints/
'''

from default.views.web.landing import WebLandingView
from flask import Blueprint

landing_blueprint = Blueprint('default', __name__)


'''
Landing
'''

landing_blueprint.add_url_rule(
    '/',
    view_func=WebLandingView.as_view('web_landing'))
