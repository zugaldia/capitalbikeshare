from flask import render_template
from flask.views import MethodView

import logging


class WebLandingView(MethodView):
    def get(self):
        return render_template('landing.html')
