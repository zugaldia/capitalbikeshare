from appython.components.sentry.sentry_client import SentryClient
from shared.config import Config


sentry_client = SentryClient(sentry_dsn=Config.SENTRY_DSN)
