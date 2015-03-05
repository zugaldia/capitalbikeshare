APP_ROOT = CapitalBikeshare
APP_ID = com.zugaldia.capitalbikeshare

all:
	@echo See Makefile for options

run-app:
	cd $(APP_ROOT); ./gradlew assembleDebug
	adb install -r $(APP_ROOT)/mobile/build/outputs/apk/mobile-debug.apk
	adb shell am start -n $(APP_ID)/$(APP_ID).MainActivity
