# Standard sample

This is the sample app for `appython` and probably the one you want to use to build your own app.

## Features

* Two different modules, based on Flask blueprints, with different scaling settings.
* The default module includes an [Ionic](http://ionicframework.com) app as frontend.
* The API module has a RESTful API built with Flask-RESTful.
* Authentication is handled by Flask-Login and a default `UserModel` is provided.
* Automatic error management is monitored by [Sentry](https://getsentry.com).
* Includes to sample API endpoints (`meta` and `user`).
* Includes a sample task endpoint.
* Includes a sample daily cron job endpoint.
* A sample script to use the Remote API.
* The deferred API is enabled by default.

Other goodies:

* Module dependencies are managed by `requirements.txt` files and set up in `appengine_config.py`.
* An automated run/build/deploy system in one `Makefile`.
* GitHub's default Python `.gitignore` is included.
* The app is ready to serve static content, securily if needed.
* Web app dependencies are managed by Bower.
* A folder shared among both modules with some App Engine utils.

## Checklists

### Requirements

* Python and Virtualenv.
* Google App Engine SDK for Python.
* Bower.
* Closure.
* SASS.

### Set up

This is one way to get your project off the ground:

1. Add this repository to your project as a submodule: `git submodule add https://github.com/zugaldia/appython.git`

2. Copy this folder (`appython/samples/standard`) into your own project and rename it to something that makes sense to you. For example: `cp -R appython/samples/standard .; mv standard appengine`

3. Update `Makefile` to set your own `PROJECT_ID`

4. Update `Makefile` and `vendor/Makefile` to set the relative path to the submodule root (`APPYTHON_ROOT`)

5. Add symlinks to `appython` and the `shared` folder: `make add-symlinks`

6. Add backend dependencies: `cd vendor; make deps`

7. Add frontend dependencies: `make add-bower`

8. Edit `app/module_default/static/partials/main.html` to change the default message

### Run locally

* Run the default module with `make run-default`, or `make run-default-clean` (for an empty database).

* Run the API module with `make run-api`, or `make run-api-clean` (for an empty database).

Point your browser at `http://localhost:8080` to see a module running
(`http://localhost:8000` for the admin interface).

### Deploy

First time only:

1. Add your project ID to the `Makefile` (`PROJECT_ID` var)

2. Rename `config.py.TEMPLATE` to `config.py` and update the `SUPERUSER_EMAIL` (to be able to create the first user), `SECRET_KEY` (you can use `scripts/generate.py` for that), and `SENTRY_DSN` (if you want error management controlled by Sentry)

Later on:

1. Do `make deploy-default` to deploy the default module (and the information in `cron.yaml`, `index.yaml`, and `queue.yaml`). It should look like this: http://com-zugaldia-appython.appspot.com

2. Do `make deploy-api` to deploy the API module. It should look like this: http://api.com-zugaldia-appython.appspot.com

### First time configuration

In order to create the superuser, visit `/api/v1/user/setup` and write down the generated password and API key.
