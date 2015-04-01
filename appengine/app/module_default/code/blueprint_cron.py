'''
Cron tasks
'''

from code.views.cron.cron_daily import CronDailyView
from flask import Blueprint

blueprint_cron = Blueprint('code', __name__)

blueprint_cron.add_url_rule(
    '/_app/cron/daily',
    view_func=CronDailyView.as_view('cron_daily'))
