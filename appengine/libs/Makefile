VIRTUALENV = /usr/local/bin/virtualenv

DEFAULT_LIBS = default_libs
MODULE_DEFAULT = ../app/module_default

all:
	@echo See Makefile for options

clean-default:
	rm -rf $(DEFAULT_LIBS)
	rm -rf $(MODULE_DEFAULT)/vendor

clean: clean-default

download-default:
	$(VIRTUALENV) $(DEFAULT_LIBS)
	$(DEFAULT_LIBS)/bin/pip install -r default-requirements.txt
	cd $(MODULE_DEFAULT); ln -s ../../libs/$(DEFAULT_LIBS)/lib/python2.7/site-packages vendor

download: download-default
