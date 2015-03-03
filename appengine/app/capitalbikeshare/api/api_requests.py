from flask.ext.restful.reqparse import RequestParser

'''
Meta
'''

echo_request = RequestParser()
echo_request.add_argument('echo', type=str, required=True)
