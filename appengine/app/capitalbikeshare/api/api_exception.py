'''
The API will always use this exception for errors. These are messages sent to
the developer and are *not* tracked by Bugsnag.
'''


class ApiException(Exception):
    pass
