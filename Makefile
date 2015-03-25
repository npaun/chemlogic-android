# Makefile: Master build instructions for the Chemlogic app.
# This file is from Chemlogic, a logic programming computer chemistry system  
# <http://icebergsystems.ca/chemlogic>  
# (C) Copyright 2012-2015 Nicholas Paun 



ANDROID = $(MAKE) -C android
SYSTEM = $(MAKE) -C system

.PHONY: app system all clean 

default: app install


app app-clean app-emulator install:
	$(ANDROID) $@

system system-clean system-install: 
	$(SYSTEM) $@

all: app system	


clean: app-clean system-clean 
