from flask import redirect
from flask.views import MethodView
from shared.config import Config

# import logging


class WebLandingView(MethodView):
    def get(self):
        return redirect(Config.API_BASE + '/meta/info')
