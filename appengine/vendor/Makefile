# Appython path
APPYTHON_ROOT = ../../appython

#
# No need to change anything after this
#

# Software reqs
PYTHON = /usr/local/bin/python
VIRTUALENV = /usr/local/bin/virtualenv

# Default
DEFAULT_MODULE_REQS = reqs-default.txt
DEFAULT_MODULE_DEPS = module_default
DEFAULT_MODULE_PATH = ../app/module_default

# API
API_MODULE_REQS = reqs-api.txt
API_MODULE_DEPS = module_api
API_MODULE_PATH = ../app/module_api

all:
	@echo See Makefile for options

#
# Default module
#

clean-default:
	rm -rf $(DEFAULT_MODULE_DEPS)
	rm -rf $(DEFAULT_MODULE_PATH)/vendor

deps-default:
	$(VIRTUALENV) $(DEFAULT_MODULE_DEPS)
	$(DEFAULT_MODULE_DEPS)/bin/pip install -r $(DEFAULT_MODULE_REQS) > $(DEFAULT_MODULE_REQS).log
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(DEFAULT_MODULE_PATH) \
		--to $(DEFAULT_MODULE_DEPS)/lib/python2.7/site-packages \
		--name vendor

#
# API module
#

clean-api:
	rm -rf $(API_MODULE_DEPS)
	rm -rf $(API_MODULE_PATH)/vendor

deps-api:
	$(VIRTUALENV) $(API_MODULE_DEPS)
	$(API_MODULE_DEPS)/bin/pip install -r $(API_MODULE_REQS) > $(API_MODULE_REQS).log
	$(PYTHON) $(APPYTHON_ROOT)/scripts/ln_plus.py \
		--from $(API_MODULE_PATH) \
		--to $(API_MODULE_DEPS)/lib/python2.7/site-packages \
		--name vendor

#
# All
#

clean: clean-default clean-api

deps: deps-default deps-api
