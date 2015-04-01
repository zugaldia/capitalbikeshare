from flask.ext.restful import reqparse

'''
User endpoint
'''

setup_request = reqparse.RequestParser()
setup_request.add_argument('password', type=unicode)
