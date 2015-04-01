from appython.components.api.common_fields import IsSetField
from appython.components.models.base_model import BaseModel
from appython.components.user.base_manager import BaseManager
from flask.ext.restful import fields, marshal
from google.appengine.ext import ndb


class UserModel(BaseModel):
    # Every user must have an email address
    email = ndb.StringProperty(required=True)

    # For email/password users
    password = ndb.StringProperty()

    # Used to authenticante API requests
    api_key = ndb.StringProperty()

    # Flags
    is_admin = ndb.BooleanProperty(default=False)

    '''
    API
    '''

    def to_api(self):
        custom_fields = {
            'email': fields.String,
            'password': IsSetField,
            'api_key': IsSetField,
            'is_admin': fields.Boolean}
        basic_fields = self.get_basic_fields()
        basic_fields.update(custom_fields)
        return marshal(self, basic_fields)

    '''
    Manual creation
    '''

    @classmethod
    def create_from_email(cls, email, password):
        email_ready = BaseManager.prepare_email(email=email)
        user_model = cls(email=email_ready)
        user_model.password = BaseManager.get_password_hash(password=password)
        return user_model

    '''
    Getters
    '''

    @classmethod
    def get_by_email(cls, email):
        email_ready = BaseManager.prepare_email(email=email)
        return cls.query(cls.email == email_ready).get()

    @classmethod
    def get_by_api_key(cls, api_key):
        return cls.query(cls.api_key == api_key).get()

    '''
    Required by Flask-Login. Note that @property doesn't work here because
    it only works with new-style classes.
    '''

    def is_active(self):
        return False if self.deleted else True

    def is_authenticated(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        # Overrides the default implementation. Flask-Login requires this
        # to be an unicode always.
        return unicode(self.key.urlsafe())
