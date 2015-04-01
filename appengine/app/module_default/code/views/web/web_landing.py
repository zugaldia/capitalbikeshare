from flask import redirect
from flask.views import MethodView

import logging


class WebLandingView(MethodView):
    def get(self):
        return redirect('https://github.com/zugaldia/capitalbikeshare')
