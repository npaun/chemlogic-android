# Makefile: Rules to build and package the Chemlogic app for Android
# This file is from Chemlogic, a logic programming computer chemistry system  
# <http://icebergsystems.ca/chemlogic>  
# (C) Copyright 2012-2015 Nicholas Paun  



.PHONY: app install app-clean

app:
	ant debug

release:
	ant release

install:
	@echo "Please wait... the command may appear to hang."
	adb wait-for-device
	adb install -r bin/Chemlogic-debug.apk

release-install:
	@echo "Please wait... the command may appear to hang."
	adb wait-for-device
	adb install -r bin/Chemlogic-release.apk

app-clean:
	ant clean


app-emulator:
	@echo "Please wait... the command may appear to hang."
	emulator @chemlogic-dev &

