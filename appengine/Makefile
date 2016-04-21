# Get one from http://console.developers.google.com
PROJECT_ID = com-zugaldia-capitalbikeshare

# Appython path
APPYTHON_ROOT = ../appython

# Runscope URL
RUNSCOPE_TRIGGER = https://api.runscope.com/radar/bucket/b23e4f92-cd07-47fa-bf37-0a0f23ceac77/trigger

#
# No need to change anything after this
#

# Software reqs
APPENGINE = /usr/local/google_appengine
PYTHON = /usr/local/bin/python

# App paths
APP_ROOT = app
MODULE_DEFAULT = $(APP_ROOT)/module_default
MODULE_API = $(APP_ROOT)/module_api

all:
	@echo See Makefile for options

#
# Preparations
#

clean-symlinks:
	cd $(MODULE_DEFAULT); rm appython shared
	cd $(MODULE_API); rm appython shared

add-symlinks:
	# Default module
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(MODULE_DEFAULT) --to $(APPYTHON_ROOT)/appython
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(MODULE_DEFAULT) --to $(APP_ROOT)/shared
	# API module
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(MODULE_API) --to $(APPYTHON_ROOT)/appython
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(MODULE_API) --to $(APP_ROOT)/shared

#
# Executed with every run/deploy
#

prepare-dev:
	$(PYTHON) $(APPYTHON_ROOT)/scripts/prepare_build.py \
		--filename=$(APP_ROOT)/shared/config.py \
		--stage=development

prepare-prod: 
	$(PYTHON) $(APPYTHON_ROOT)/scripts/prepare_build.py \
		--filename=$(APP_ROOT)/shared/config.py \
		--stage=production --increase-build=True

#
# Default module
#

run-default: prepare-dev
	@echo "Running default module..."
	cd $(MODULE_DEFAULT); dev_appserver.py . \
		--require_indexes

run-default-clean: prepare-dev
	@echo "Running default module (clean)..."
	cd $(MODULE_DEFAULT); dev_appserver.py . \
		--clear_datastore --require_indexes

deploy-default: prepare-prod
	@echo "Deploying default module..."
	cd $(MODULE_DEFAULT); $(PYTHON) $(APPENGINE)/appcfg.py \
		--noauth_local_webserver \
		--application=$(PROJECT_ID) \
		update .

#
# API module
#

run-api: prepare-dev
	@echo "Running API module..."
	cd $(MODULE_API); dev_appserver.py api.yaml \
		--require_indexes

run-api-clean: prepare-dev
	@echo "Running API module (clean)..."
	cd $(MODULE_API); dev_appserver.py api.yaml \
		--clear_datastore --require_indexes

deploy-api: prepare-prod
	@echo "Deploying API module..."
	cd $(MODULE_API); $(PYTHON) $(APPENGINE)/appcfg.py \
		--noauth_local_webserver \
		--application=$(PROJECT_ID) \
		update api.yaml
	@echo "Triggering Runscope tests..."
	curl $(RUNSCOPE_TRIGGER) > /dev/null
