# Required to run locally
PYTHON = /usr/local/bin/python
APPENGINE = /usr/local/google_appengine

# App paths
APP_ROOT = app
MODULE_DEFAULT = module_default

# App Engine ID
APP_ID = com-zugaldia-capitalbikeshare

all:
	@echo See Makefile for options

#
# Default module
#

run-default:
	@echo "Running default module..."
	cd $(APP_ROOT)/$(MODULE_DEFAULT); dev_appserver.py . \
		--require_indexes

run-default-clean:
	@echo "Running default module (datastore cleared)..."
	cd $(APP_ROOT)/$(MODULE_DEFAULT); dev_appserver.py . \
		--clear_datastore --require_indexes

deploy-default:
	@echo Uploading default module...
	cd $(APP_ROOT)/$(MODULE_DEFAULT); $(PYTHON) $(APPENGINE)/appcfg.py \
		--noauth_local_webserver --oauth2 \
		--application=$(APP_ID) \
		update .
