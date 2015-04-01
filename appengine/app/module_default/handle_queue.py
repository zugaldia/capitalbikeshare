from code.blueprint_queue import blueprint_queue
from shared.bootstrap import create_app
from shared.config import Config

'''
Flask set up
'''

app = create_app(config_flask=Config.FLASK)
app.register_blueprint(blueprint_queue)
